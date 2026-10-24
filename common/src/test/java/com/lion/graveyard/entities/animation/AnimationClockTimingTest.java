package com.lion.graveyard.entities.animation;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.LoopType;
import software.bernie.geckolib.animation.object.PlayState;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lion.graveyard.support.HeadlessAnimator;
import com.lion.graveyard.support.HeadlessAnimator.Frame;
import com.lion.graveyard.support.HeadlessAnimator.Point;
import com.lion.graveyard.support.MobAnimations;
import com.lion.graveyard.support.ModProject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimationClockTimingTest {
    private static final int BLEND = MobAnimations.TRANSITION_TICKS;
    private static final double EPSILON = 1.0E-6D;
    private static final String GHOUL = "ghoul/ghoul.animation.json";
    private static final String LICH = "lich/lich.animation.json";

    private record Event(double tick, String animation, double offset) {}

    private static final class Actor implements GeoAnimatable, AnimationClock.KeyframeZeroSink {
        private final Function<Actor, AnimationController<Actor>> factory;
        private final List<Event> events = new ArrayList<>();
        private HeadlessAnimator<Actor> animator;
        private RawAnimation want;
        private boolean stop;
        private boolean spawning;
        private int phase = 1;
        private int spawnElapsed;

        Actor(Function<Actor, AnimationController<Actor>> factory) {
            this.factory = factory;
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
            registrar.add(this.factory.apply(this));
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return null;
        }

        @Override
        public void keyframeZero(String animation, double startOffsetTicks) {
            this.events.add(new Event(this.animator.age(), animation, startOffsetTicks));
        }

        List<Frame> tick(int tick) {
            return this.animator.tick(tick);
        }
    }

    private static Actor ghoul(String first) {
        Actor actor = new Actor(self -> AnimationClock.controller("controller", BLEND, test -> {
            test.controller().setTransitionTicks(test.controller().getCurrentAnimationPoint() == null ? 0 : BLEND);
            test.controller().setAnimation(self.want);

            return PlayState.CONTINUE;
        }));
        actor.want = loop(first);

        return attach(actor, GHOUL);
    }

    private static Actor lich(int spawnElapsed, boolean spawning) {
        Actor actor = new Actor(self -> {
            RawAnimation spawn = RawAnimation.begin().then("spawn", LoopType.HOLD_ON_LAST_FRAME);

            return AnimationTable.<Actor>create()
                    .transitionTicks(() -> self.spawning ? 0 : AnimationTable.TRANSITION_TICKS)
                    .stopWhen(() -> self.stop)
                    .when(() -> self.spawning, spawn)
                    .startAt(spawn, a -> a.spawning ? a.spawnElapsed : 0)
                    .when(() -> self.phase == 2, RawAnimation.begin().then("phase_two", LoopType.HOLD_ON_LAST_FRAME))
                    .otherwise(loop("idle"))
                    .build("controller", 0);
        });
        actor.spawnElapsed = spawnElapsed;
        actor.spawning = spawning;

        return attach(actor, LICH);
    }

    private static Actor attach(Actor actor, String animationFile) {
        String json = ModProject.read(MobAnimations.ANIMATIONS.resolve(animationFile));
        actor.animator = new HeadlessAnimator<>(actor, "controller", json);

        return actor;
    }

    private static RawAnimation loop(String clip) {
        return RawAnimation.begin().then(clip, LoopType.LOOP);
    }

    private static double clipTicks(String animationFile, String clip) {
        JsonObject animations = JsonParser
                .parseString(ModProject.read(MobAnimations.ANIMATIONS.resolve(animationFile)))
                .getAsJsonObject()
                .getAsJsonObject("animations");

        return animations.getAsJsonObject(clip).get("animation_length").getAsDouble() * 20.0D;
    }

    private static void assertEventAt(Event event, String animation, double expectedTick) {
        assertEquals(animation, event.animation());
        assertTrue(
                event.tick() >= expectedTick - EPSILON && event.tick() <= expectedTick + HeadlessAnimator.FRAME_STEP + EPSILON,
                animation + " keyframe 0 at " + event.tick() + ", expected " + expectedTick
        );
    }

    @Test
    void keyframeZeroFollowsBlendLoopsAndRestart() {
        double walk = clipTicks(GHOUL, "animation.ghoul.walk");
        double idle = clipTicks(GHOUL, "animation.ghoul.idle");
        int switchTick = 80;
        int restartTick = switchTick + BLEND + (int) idle + 9;
        Actor actor = ghoul("animation.ghoul.walk");

        for (int tick = 0; tick < restartTick + BLEND + 6; tick++) {
            if (tick == switchTick) {
                actor.want = loop("animation.ghoul.idle");
            }

            if (tick == restartTick) {
                AnimationClock.restart(actor.animator.controller());
            }

            actor.tick(tick);
        }

        List<Event> events = actor.events;
        int index = 0;

        for (int loops = 0; loops * walk < switchTick; loops++) {
            assertEventAt(events.get(index++), "animation.ghoul.walk", loops * walk);
        }

        assertEventAt(events.get(index++), "animation.ghoul.idle", switchTick + BLEND);
        assertEventAt(events.get(index++), "animation.ghoul.idle", switchTick + BLEND + idle);
        assertEventAt(events.get(index++), "animation.ghoul.idle", restartTick + BLEND);
        assertEquals(index, events.size(), "unexpected extra keyframe 0 events");
    }

    @Test
    void spawnClipStartsAtItsFirstFrameWithoutBlend() {
        Actor actor = lich(0, true);
        List<Frame> first = actor.tick(0);

        assertEquals("spawn", first.get(0).point().clip());
        assertEquals(0.0D, first.get(0).point().animTime(), EPSILON);

        for (int tick = 1; tick < 8; tick++) {
            actor.tick(tick);
        }

        assertEquals(1, actor.events.size());
        assertEventAt(actor.events.get(0), "spawn", 0.0D);
        assertEquals(0.0D, actor.events.get(0).offset(), EPSILON);
    }

    @Test
    void spawnClipStartsLateAtTheGivenOffset() {
        int offset = 30;
        Actor actor = lich(offset, true);
        List<Frame> first = actor.tick(0);

        assertEquals("spawn", first.get(0).point().clip());
        assertEquals(offset / 20.0D, first.get(0).point().animTime(), EPSILON);

        for (int tick = 1; tick < 8; tick++) {
            actor.tick(tick);
        }

        assertEquals(1, actor.events.size());
        assertEquals(offset, actor.events.get(0).offset(), EPSILON);
    }

    private record LichRun(List<Frame> beforeStop, List<List<Frame>> stopped, List<Frame> resume, Actor actor) {}

    private static LichRun lichStopAndResume(int stopTicks) {
        int stopTick = 20;
        int resumeTick = stopTick + stopTicks;
        Actor actor = lich(0, false);
        List<Frame> beforeStop = null;
        List<List<Frame>> stopped = new ArrayList<>();
        List<Frame> resume = null;

        for (int tick = 0; tick < resumeTick + BLEND + 6; tick++) {
            actor.stop = tick >= stopTick && tick < resumeTick;

            if (tick == resumeTick) {
                actor.phase = 2;
            }

            List<Frame> frames = actor.tick(tick);

            if (tick == stopTick - 1) {
                beforeStop = frames;
            } else if (actor.stop) {
                stopped.add(frames);
            } else if (tick == resumeTick) {
                resume = frames;
            }
        }

        return new LichRun(beforeStop, stopped, resume, actor);
    }

    @Test
    void stopKeepsThePoseAndBlendsBackOut() {
        LichRun run = lichStopAndResume(8);
        Point held = run.beforeStop().get(2).point();

        assertEquals("idle", held.clip());

        for (int tick = 0; tick < BLEND; tick++) {
            for (Frame frame : run.stopped().get(tick)) {
                Point now = frame.point();

                assertEquals(held.clip(), now.clip(), "tick " + frame.tick());
                assertEquals(held.animTime(), now.animTime(), EPSILON, "tick " + frame.tick());
                assertTrue(frame.animatingBones(), "bones released at tick " + frame.tick());
            }
        }
    }

    @Test
    void phaseTwoResumesFromThePreStopPose() {
        LichRun run = lichStopAndResume(3);
        Point held = run.beforeStop().get(2).point();
        Frame first = run.resume().get(0);
        Point from = first.previous();

        assertEquals("phase_two", first.point().clip());
        assertNotNull(from);
        assertEquals(held.clip(), from.clip());
        assertEquals(held.animTime(), from.animTime(), EPSILON);
        assertEquals(BLEND, first.transitionTicks());

        List<Event> phaseTwo = run
                .actor()
                .events
                .stream()
                .filter(e -> e.animation().equals("phase_two"))
                .toList();

        assertEquals(1, phaseTwo.size());
        assertEventAt(phaseTwo.get(0), "phase_two", 23 + BLEND);
    }
}

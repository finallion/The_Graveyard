package com.lion.graveyard.support;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationPoint;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.cache.animation.Animation;
import com.geckolib.cache.animation.BakedAnimations;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.loading.definition.animation.ActorAnimations;
import com.geckolib.loading.math.MathParser;
import com.geckolib.loading.math.MolangQueries;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// Drives a real GeckoLib controller against a mod animation file without starting Minecraft.
public final class HeadlessAnimator<T extends GeoAnimatable> {
    public static final float[] PARTIALS = {0.0F, 0.34F, 0.67F};
    public static final double FRAME_STEP = 0.34D;

    private static final double BASE_AGE = 1000.0D;

    private final T animatable;
    private final AnimatableManager<T> manager;
    private final AnimationController<T> controller;
    private final GeoModel<T> model;
    private final FrameState state = new FrameState();

    public record Point(String clip, double animTime) {}

    public record Frame(
            double tick,
            Point point,
            Point previous,
            double transitionTime,
            int transitionTicks,
            boolean animatingBones
    ) {
    }

    public HeadlessAnimator(T animatable, String controllerName, String animationJson) {
        BakedAnimations baked = ActorAnimations.GSON
                .fromJson(JsonParser.parseString(animationJson).getAsJsonObject(), ActorAnimations.class)
                .bake(null, MathParser.create());
        this.animatable = animatable;
        this.manager = new AnimatableManager<>(animatable);
        this.controller = this.manager.getAnimationControllers().get(controllerName);
        this.model = new GeoModel<>() {
            @Override
            public Identifier getModelResource(GeoRenderState renderState) {
                return null;
            }

            @Override
            public Identifier getTextureResource(GeoRenderState renderState) {
                return null;
            }

            @Override
            public Identifier getAnimationResource(T animatable) {
                return null;
            }

            @Override
            public Animation getBakedAnimation(T animatable, String name) {
                return baked.getAnimation(name);
            }
        };
    }

    public AnimationController<T> controller() {
        return this.controller;
    }

    public double age() {
        return this.state.age - BASE_AGE;
    }

    public List<Frame> tick(int tick) {
        List<Frame> frames = new ArrayList<>();

        for (float partial : PARTIALS) {
            frames.add(frame(tick, partial));
        }

        return frames;
    }

    private Frame frame(int tick, float partial) {
        this.state.age = BASE_AGE + tick + partial;
        this.state.partial = partial;
        MolangQueries.Actor<T> actor = new MolangQueries.Actor<>(
                this.animatable,
                this.state,
                this.controller,
                0,
                partial,
                null,
                null,
                null
        );
        ControllerState result = this.controller.extractControllerState(
                this.animatable,
                this.state,
                this.manager,
                actor,
                this.model
        );

        if (result == null) {
            return new Frame(tick + partial, null, null, -1.0D, 0, this.controller.isAnimatingBones());
        }

        return new Frame(
                tick + partial,
                point(result.animationPoint()),
                point(result.prevAnimationPoint()),
                result.transitionTime(),
                result.transitionTicks(),
                this.controller.isAnimatingBones()
        );
    }

    private static Point point(AnimationPoint point) {
        return point == null ? null : new Point(point.animation().name(), point.animTime());
    }

    private static final class FrameState implements GeoRenderState {
        private final Map<DataTicket<?>, Object> data = new HashMap<>();
        private double age;
        private float partial;

        @Override
        public Map<DataTicket<?>, Object> getDataMap() {
            return this.data;
        }

        @Override
        public double getAnimatableAge() {
            return this.age;
        }

        @Override
        public float getPartialTick() {
            return this.partial;
        }
    }
}

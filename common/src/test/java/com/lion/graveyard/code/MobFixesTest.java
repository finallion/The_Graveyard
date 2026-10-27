package com.lion.graveyard.code;

import com.lion.graveyard.support.JavaText;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobFixesTest {
    private static String code(String relative) {
        return Sources.code(ModProject.JAVA.resolve(relative));
    }

    private static String body(String relative, String method) {
        return JavaText.joinedBodies(JavaText.methods(code(relative)), method);
    }

    @Test
    void lichPhaseOneKeepsItsMeleeGoalRunning() {
        String goal = code("entities/ai/goals/LichMeleeGoal.java");
        String hold = body("entities/ai/goals/LichMeleeGoal.java", "isPhaseOneHold");

        assertTrue(body("entities/ai/goals/LichMeleeGoal.java", "canContinueToUse").contains("isPhaseOneHold(target)"));
        assertTrue(hold.contains("getPhase() == 1"));
        assertTrue(hold.contains("NO_CREATIVE_OR_SPECTATOR"));
        assertFalse(hold.contains("getSquaredMaxAttackDistance"));
        assertTrue(body("entities/ai/goals/LichMeleeGoal.java", "tick").contains("!isStraightBelowOrAbove(target)"));
        assertTrue(body("entities/ai/goals/LichMeleeGoal.java", "tick").contains("setYRot(this.mob.yHeadRot)"));
        assertTrue(goal.contains("this.mob.getNavigation().stop();"));
    }

    @Test
    void meleeGoalRechecksWhenTheTargetChangesHeight() {
        String reset = body("entities/ai/goals/AbstractGraveyardMeleeGoal.java", "isNewOrFarMoved");

        assertTrue(reset.contains("checkedTargetY"));
        assertTrue(reset.contains("dy * dy"));
    }

    @Test
    void meleeGoalSkipsTheThrottleWithinReach() {
        String canUse = body("entities/ai/goals/AbstractGraveyardMeleeGoal.java", "canUse");

        assertTrue(canUse.contains("this.isNewOrFarMoved(target) || this.isInReach(target)"));
    }

    @Test
    void meleeGoalThrottleDoesNotRecurse() {
        String canUse = body("entities/ai/goals/AbstractGraveyardMeleeGoal.java", "canUse");

        assertTrue(canUse.contains("isNewOrFarMoved"));
        assertFalse(canUse.contains("canUse()"));
        assertFalse(canUse.contains("lastUpdateTime -="));
    }

    @Test
    void staffCallKeepsTheBondAndSearchesAllLevels() {
        String call = body("item/BoneStaffItem.java", "call");

        assertFalse(call.contains("bindings.remove"));
        assertFalse(call.contains(".remove(ghoulingId)"));
        assertTrue(call.contains("entity.graveyard.ghouling.not_found"));
        assertTrue(call.indexOf("findLoaded(") >= 0);
        assertTrue(call.indexOf("findLoaded(") < call.indexOf("getLevel(binding.dimension())"));
    }

    @Test
    void staffTellsWhenTheFetchGivesUp() {
        String code = code("item/BoneStaffItem.java");
        String tick = JavaText.joinedBodies(JavaText.methods(code), "inventoryTick");

        assertTrue(tick.contains("entity.graveyard.ghouling.gave_up"));
        assertTrue(tick.contains("misses + 1 == MAX_FETCH_MISSES"));
        assertTrue(code.contains("SIT_CLICK_TICKS = 8;"));
        assertTrue(code.contains("STORE_TICKS = 30;"));
    }

    @Test
    void namelessHangedExposesItsTradesToTheVtpButtons() {
        String code = code("entities/NamelessHangedEntity.java");

        assertTrue(code.contains("implements GeoEntity, TradeControl"));
        assertTrue(body("entities/NamelessHangedEntity.java", "rerollAll").contains("updateTrades("));
        assertTrue(body("entities/NamelessHangedEntity.java", "catalogPool").contains("TRADES_REGISTRY"));
        assertTrue(body("entities/NamelessHangedEntity.java", "catalogMaxLevel").contains("return 1;"));
        assertTrue(body("entities/NamelessHangedEntity.java", "catalogPicks").contains("maxOffers()"));
        assertFalse(code.contains("supportsLevels"));
    }

    @Test
    void lichMeleeSoundStaysOutOfTheVanillaAttackSoundHook() {
        assertFalse(code("entities/LichEntity.java").contains("void playAttackSound("));
        assertTrue(body("entities/ai/goals/LichMeleeGoal.java", "attack").contains("playMeleeSwingSound()"));
    }

    @Test
    void hordeTriesSeveralSpotsForTheCaptain() {
        String file = "entities/horde/GraveyardHordeSpawner.java";
        String tick = body(file, "tick");

        assertTrue(code(file).contains("CAPTAIN_SPOTS = 10;"));
        assertTrue(tick.contains("spot < CAPTAIN_SPOTS"));
        assertTrue(tick.contains("spawnHordeEntity(world, mutable, random, true, illagerSpawn)"));
        assertTrue(body(file, "spawnHordeEntity").contains("canSpawnInDarkness"));
    }

    @Test
    void sarcophagusRenderBoxCoversBothHalves() {
        String box = body("blockentities/renders/SarcophagusBlockEntityRenderer.java", "getRenderBoundingBox");

        assertTrue(code("blockentities/renders/SarcophagusBlockEntityRenderer.java").contains("AABB getRenderBoundingBox(BlockEntity blockEntity)"));
        assertTrue(box.contains(".inflate(1.0D)"));
        assertTrue(box.contains(".expandTowards(0.0D, 1.0D, 0.0D)"));
    }

    @Test
    void chronicleToastUsesTheVanillaSprite() {
        String toast = code("chronicle/client/ChronicleToast.java");

        assertFalse(toast.contains("toasts.png"));
        assertTrue(toast.contains("toast/advancement"));
        assertTrue(toast.contains("blitSprite(SPRITE"));
    }

    @Test
    void minionStopsSittingPoseWhenItStopsStaying() {
        assertTrue(body("entities/GraveyardMinionEntity.java", "setSitting").contains("setInSittingPose(false)"));
    }

    @Test
    void wraithLetsKillAndVoidDamageThrough() {
        String body = body("entities/WraithEntity.java", "isInvulnerableTo");

        assertTrue(body.indexOf("BYPASSES_INVULNERABILITY") >= 0);
        assertTrue(body.indexOf("BYPASSES_INVULNERABILITY") < body.indexOf("BYPASSES_ARMOR"));
    }

    @Test
    void ghoulDamageLandsOnTheVisibleHitFrame() {
        assertTrue(code("entities/ai/goals/GhoulMeleeAttackGoal.java").contains("DAMAGE_START_IN_ANIM = 10;"));
    }

    @Test
    void fallingCorpseLetsKillAndVoidDamageThrough() {
        String body = body("entities/FallingCorpse.java", "hurt");

        assertTrue(body.indexOf("BYPASSES_INVULNERABILITY") >= 0);
        assertTrue(body.indexOf("BYPASSES_INVULNERABILITY") < body.indexOf("BYPASSES_RESISTANCE"));
    }

    @Test
    void meleeMobsEndTheAttackStateAtTheLastHitFrame() {
        assertTrue(code("entities/RevenantEntity.java").contains("ATTACK_ANIMATION_DURATION = 10;"));
        assertTrue(code("entities/NightmareEntity.java").contains("ATTACK_ANIMATION_DURATION = 10;"));
        assertTrue(code("entities/GhoulingEntity.java").contains("ATTACK_ANIMATION_DURATION = 12;"));
        assertTrue(code("entities/ai/goals/RevenantMeleeAttackGoal.java").contains("DAMAGE_START_IN_ANIM = 1;"));
        assertTrue(code("entities/ai/goals/NightmareMeleeAttackGoal.java").contains("DAMAGE_START_IN_ANIM = 0;"));
        assertTrue(code("entities/ai/goals/GhoulingMeleeAttackGoal.java").contains("DAMAGE_START_IN_ANIM = 0;"));
    }

    @Test
    void meleeMobsCheckReachBeforeTheHit() {
        for (String goal : new String[] {"Revenant", "Nightmare", "Ghouling", "Ghoul"}) {
            String code = code("entities/ai/goals/" + goal + "MeleeAttackGoal.java");

            assertTrue(code.contains("distanceToSqr(target) <= d + 1.0D"), goal);
            assertTrue(code.indexOf("distanceToSqr(target) <= d + 1.0D") < code.indexOf("doHurtTarget"), goal);
        }

        assertTrue(code("entities/ai/goals/LichMeleeGoal.java").contains("distanceToSqr(target) <= HIT_REACH_SQUARED && this.mob.doHurtTarget"));
        assertTrue(code("entities/ai/goals/LichMeleeGoal.java").contains("HIT_REACH_SQUARED = 36.0D"));
    }

    @Test
    void ghoulingDoesNotRunInWaterAndDropsStaleAggression() {
        assertTrue(code("entities/GhoulingEntity.java").contains("isAggressive() && !isInWater() && AnimationTable.isMoving(state)"));
        assertTrue(body("entities/GhoulingEntity.java", "customServerAiStep").contains("setAggressive(false)"));
    }

    @Test
    void illagerAndWraithKeepTheirMeleeGoalWhileInReach() {
        for (String file : new String[] {"entities/CorruptedIllager.java", "entities/WraithEntity.java"}) {
            String body = body(file, "canContinueToUse");

            assertTrue(body.contains("NO_CREATIVE_OR_SPECTATOR"), file);
            assertTrue(body.contains("this.mob.getTarget()"), file);
        }
    }

    @Test
    void illagerIgnoresNewHitsWhileOneIsPending() {
        String body = body("entities/AnimatedCorruptedIllager.java", "doHurtTarget");

        assertTrue(body.contains("this.pendingTarget != null"));
        assertTrue(body.indexOf("this.pendingTarget != null") < body.indexOf("ATTACK_KIND"));
    }

    @Test
    void staffRecallTellsWhenItStopsAnAttack() {
        String call = body("item/BoneStaffItem.java", "call");

        assertTrue(call.contains("entity.graveyard.ghouling.stop_attack"));
        assertTrue(call.indexOf("wasFighting =") < call.indexOf("fetch("));
    }

    @Test
    void tormentedVillagerWalksOnTheSmoothedLimbSpeed() {
        String code = code("entities/TormentedVillagerEntity.java");

        assertTrue(code.contains("AnimationTable.isMoving(state) ? Mood.WALK"));
        assertFalse(code.contains("this.getX() - this.xo"));
    }
}

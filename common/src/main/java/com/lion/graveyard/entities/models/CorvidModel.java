package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationPoint;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class CorvidModel extends GeoModel<CorvidEntity> {
    private static final Identifier ANIMATION = Graveyard.id("corvid/corvid");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return renderState.getGeckolibData(GraveyardDataTickets.CORVID_SPECIES).getModel();
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return renderState.getGeckolibData(GraveyardDataTickets.CORVID_SPECIES).getTexture();
    }

    @Override
    public Identifier getAnimationResource(CorvidEntity entity) {
        return ANIMATION;
    }

    @Override
    public void addAdditionalStateData(
            CorvidEntity animatable,
            @Nullable Object relatedObject,
            GeoRenderState renderState
    ) {
        // The additive look rotation fights the head motion of the peck animation.
        float partialTick = renderState.getGeckolibData(DataTickets.PARTIAL_TICK);
        boolean peck = this.isPlayingPeck(renderState.getGeckolibData(DataTickets.ANIMATABLE_MANAGER));
        renderState.addGeckolibData(GraveyardDataTickets.CORVID_SPECIES, animatable.getSpecies());
        renderState.addGeckolibData(GraveyardDataTickets.CORVID_LOOK_WEIGHT, animatable.stepLookWeight(peck, partialTick));
    }

    private boolean isPlayingPeck(AnimatableManager<?> manager) {
        AnimationController<?> controller = manager.getAnimationControllers().get("controller");

        if (controller == null) {
            return false;
        }

        AnimationPoint current = controller.getCurrentAnimationPoint();
        return current != null && current.animation().name().endsWith(".peck");
    }
}

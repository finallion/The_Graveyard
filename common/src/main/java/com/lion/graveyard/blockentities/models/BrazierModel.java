package com.lion.graveyard.blockentities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.BrazierBlockEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class BrazierModel extends GeoModel<BrazierBlockEntity> {
    @Override
    public Identifier getAnimationResource(BrazierBlockEntity entity) {
        return Graveyard.id("brazier");
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("brazier");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/block/brazier.png");
    }
}

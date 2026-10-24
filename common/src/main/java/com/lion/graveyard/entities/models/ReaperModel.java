package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ReaperEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class ReaperModel extends GeoModel<ReaperEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("reaper");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/reaper.png");
    }

    @Override
    public Identifier getAnimationResource(ReaperEntity animatable) {
        return Graveyard.id("reaper/reaper");
    }


}

package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.TormentedVillagerEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class TormentedVillagerModel extends GeoModel<TormentedVillagerEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("tormented_villager");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/tormented_villager.png");
    }

    @Override
    public Identifier getAnimationResource(TormentedVillagerEntity entity) {
        return Graveyard.id("tormented_villager/tormented_villager");
    }
}

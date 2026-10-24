package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.WraithEntity;
import com.lion.graveyard.entities.animation.BoneReturn;
import com.lion.graveyard.entities.models.WraithModel;
import com.lion.graveyard.entities.renders.features.GraveyardEyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class WraithRenderer<R extends LivingEntityRenderState> extends GeoEntityRenderer<WraithEntity, R> {
    private static final Identifier EYES = Graveyard.id("textures/entity/wraith_eyes.png");

    public WraithRenderer(EntityRendererProvider.Context context) {
        super(context, new WraithModel());
        this.withRenderLayer(new GraveyardEyesLayer<>(this, EYES));
        this.shadowRadius = 0.3F;
    }

    @Override
    public void applyAnimationControllers(RenderPassInfo<R> renderPassInfo, BoneSnapshots boneSnapshots) {
        super.applyAnimationControllers(renderPassInfo, boneSnapshots);
        BoneReturn.apply(renderPassInfo, boneSnapshots);
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

}

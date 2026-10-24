package com.lion.graveyard.gui;

import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Unit;

/// The vanilla sign renderer supports wood types only.
@Environment(EnvType.CLIENT)
public class GravestoneSignRenderer extends PictureInPictureRenderer<GravestoneSignRenderState> {

    public GravestoneSignRenderer() {}

    @Override
    public Class<GravestoneSignRenderState> getRenderStateClass() {
        return GravestoneSignRenderState.class;
    }

    @Override
    protected void renderToTexture(
            GravestoneSignRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector
    ) {
        Minecraft.getInstance()
            .gameRenderer
            .lighting()
            .setupFor(Lighting.Entry.ITEMS_FLAT);
        poseStack.translate(0.0F, -0.75F, 0.0F);
        collector.submitModel(
                state.model(),
                Unit.INSTANCE,
                poseStack,
                GravestoneBlockEntityRenderer.getRenderType(state.block()),
                15728880,
                OverlayTexture.NO_OVERLAY,
                -1,
                null
        );
    }

    @Override
    protected String getTextureLabel() {
        return "graveyard gravestone sign";
    }
}

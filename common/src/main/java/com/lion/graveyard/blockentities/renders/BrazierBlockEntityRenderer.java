package com.lion.graveyard.blockentities.renders;


import com.lion.graveyard.blockentities.BrazierBlockEntity;
import com.lion.graveyard.blockentities.models.BrazierModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class BrazierBlockEntityRenderer<R extends BlockEntityRenderState> extends GeoBlockRenderer<BrazierBlockEntity, R> {

    public BrazierBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new BrazierModel());
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }
}

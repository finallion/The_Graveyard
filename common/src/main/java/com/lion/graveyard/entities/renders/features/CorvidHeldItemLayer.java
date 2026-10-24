package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.entities.CorvidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;

public class CorvidHeldItemLayer<R extends GeoRenderState> extends BlockAndItemGeoLayer<CorvidEntity, Void, R> {

    public CorvidHeldItemLayer(EntityRendererProvider.Context context, GeoRenderer<CorvidEntity, Void, R> renderer) {
        super(context, renderer);
    }

    @Override
    protected List<RenderData> getRelevantBones(
            CorvidEntity animatable,
            @Nullable Void relatedObject,
            R renderState,
            float partialTick
    ) {
        ItemStackRenderState held = RenderUtil.createRenderStateForItem(
                animatable.getItemBySlot(EquipmentSlot.MAINHAND),
                this.itemModelResolver,
                ItemDisplayContext.GROUND,
                animatable
        );

        return List.of(RenderData.item("head", ItemDisplayContext.GROUND, held));
    }

    @Override
    public void addRenderData(CorvidEntity animatable, @Nullable Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(CONTENTS, getRelevantBones(animatable, relatedObject, renderState, partialTick));
    }

    @Override
    protected void submitItemStackRender(
            PoseStack poseStack,
            GeoBone bone,
            ItemStackRenderState stackState,
            ItemDisplayContext displayContext,
            R renderState,
            SubmitNodeCollector renderTasks,
            int packedLight
    ) {
        // Beak tip: head pivot at z=-6, beak end at z=-9, two units above the pivot.
        poseStack.translate(0.0F, 0.09F, -0.24F);
        poseStack.rotateDegrees(Axis.XP, 90.0F);
        poseStack.rotateDegrees(Axis.YP, 180.0F);
        poseStack.scale(0.6F, 0.6F, 0.6F);
        super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks, packedLight);
    }
}

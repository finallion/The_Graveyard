package com.lion.graveyard.world.processors;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/// SinglePoolElementMixin disables fluid placement for pieces that use this processor.
public class RemoveWaterloggedCryptProcessor implements StructureProcessor {
    public static final MapCodec<RemoveWaterloggedCryptProcessor> CODEC = MapCodec.unit(RemoveWaterloggedCryptProcessor::new);

    public RemoveWaterloggedCryptProcessor() { }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader world,
            BlockPos pos,
            BlockPos pos2,
            BlockPos originalRelativePos,
            StructureTemplate.StructureBlockInfo infoIn2,
            StructurePlaceSettings settings
    ) {
        return infoIn2;
    }

    @Override
    public MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }
}

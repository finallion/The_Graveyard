package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

public class TGDoorBlock extends DoorBlock {

    public TGDoorBlock(Properties settings) {
        super(BlockSetType.IRON, settings);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (!Graveyard.getConfig().flag("darkIronOpensByHand", false)) {
            return super.useWithoutItem(state, level, pos, player, hit);
        }

        this.setOpen(player, level, state, pos, !state.getValue(OPEN));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}

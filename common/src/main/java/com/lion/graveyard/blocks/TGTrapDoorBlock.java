package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public class TGTrapDoorBlock extends TrapDoorBlock {

    public TGTrapDoorBlock(Properties settings) {
        super(settings, BlockSetType.IRON);
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (hand != InteractionHand.MAIN_HAND || !Graveyard.getConfig().flag("darkIronOpensByHand", false)) {
            return super.use(state, level, pos, player, hand, hit);
        }

        BlockState toggled = state.cycle(OPEN);
        level.setBlock(pos, toggled, 2);

        if (toggled.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        this.playSound(player, level, pos, toggled.getValue(OPEN));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}

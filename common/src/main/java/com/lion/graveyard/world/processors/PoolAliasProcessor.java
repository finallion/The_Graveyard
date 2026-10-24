package com.lion.graveyard.world.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.JigsawBlockInfo;

/// This processor changes no block. The pool element reads it when it lists its jigsaw blocks.
public class PoolAliasProcessor implements StructureProcessor {

    public static final MapCodec<PoolAliasProcessor> CODEC = Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC)
            .fieldOf("aliases").xmap(PoolAliasProcessor::new, processor -> processor.aliases);

    private final Map<Identifier, Identifier> aliases;

    public PoolAliasProcessor(Map<Identifier, Identifier> aliases) {
        this.aliases = aliases;
    }

    public List<JigsawBlockInfo> apply(List<JigsawBlockInfo> jigsaws) {
        List<JigsawBlockInfo> result = new ArrayList<>(jigsaws.size());

        for (JigsawBlockInfo jigsaw : jigsaws) {
            Identifier alias = this.aliases.get(jigsaw.pool().identifier());

            if (alias == null) {
                result.add(jigsaw);
                continue;
            }

            result.add(new JigsawBlockInfo(
                    jigsaw.pos(),
                    jigsaw.state(),
                    jigsaw.jointType(),
                    jigsaw.name(),
                    ResourceKey.create(Registries.TEMPLATE_POOL, alias),
                    jigsaw.target(),
                    jigsaw.placementPriority(),
                    jigsaw.selectionPriority()
            ));
        }

        return result;
    }

    @Override
    public MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }
}

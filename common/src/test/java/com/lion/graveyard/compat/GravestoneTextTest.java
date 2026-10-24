package com.lion.graveyard.compat;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.entity.SignText;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GravestoneTextTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static ListTag lines(String... texts) {
        ListTag list = new ListTag();

        for (String text : texts) {
            list.add(StringTag.valueOf(text));
        }

        return list;
    }

    private static CompoundTag frontText(ListTag messages, ListTag filtered) {
        CompoundTag tag = new CompoundTag();
        tag.put("messages", messages);

        if (filtered != null) {
            tag.put("filtered_messages", filtered);
        }

        tag.putString("color", "black");
        tag.putBoolean("has_glowing_text", false);
        return tag;
    }

    private static List<String> strings(List<Component> lines) {
        return lines.stream()
                .map(Component::getString)
                .toList();
    }

    private static SignText parse(CompoundTag tag) {
        return SignText.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }

    @Test
    void storedFrontTextWithoutFilteredLinesStillReads() {
        SignText text = parse(frontText(lines("Here", "lies", "Bob", ""), null));
        List<String> raw = strings(text.getMessages(false));

        assertEquals(List.of("Here", "lies", "Bob", ""), raw);
        assertEquals(raw, strings(text.getMessages(true)));
    }

    @Test
    void storedFrontTextWithEqualFilteredLinesKeepsThem() {
        SignText text = parse(frontText(lines("a", "b", "c", "d"), lines("a", "b", "x", "d")));

        assertEquals("x", text.getMessages(true).get(2).getString());
        assertEquals("c", text.getMessages(false).get(2).getString());
    }

    @Test
    void unequalFilteredLinesDoNotLoseTheText() {
        CompoundTag stored = frontText(lines("a", "b", "c", "d"), lines("a", "b"));

        assertTrue(SignText.CODEC.parse(NbtOps.INSTANCE, stored).result().isPresent());

        SignText text = parse(stored);

        assertEquals("c", text.getMessages(false).get(2).getString());
        assertEquals("c", text.getMessages(true).get(2).getString());
        assertEquals("black", text.getColor().getSerializedName());
    }

    @Test
    void writtenTextReadsBackUnchanged() {
        SignText original = parse(frontText(lines("1", "2", "3", "4"), null)).withGlowingText(true);
        CompoundTag written = (CompoundTag) SignText.CODEC.encodeStart(NbtOps.INSTANCE, original).getOrThrow();
        SignText read = parse(written);

        assertEquals(original, read);
    }
}

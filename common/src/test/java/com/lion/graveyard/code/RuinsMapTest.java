package com.lion.graveyard.code;

import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuinsMapTest {
    @Test
    void ruinsMapIsRegisteredLikeVanillaTypedMaps() {
        String items = Sources.code(ModProject.JAVA.resolve("init/TGItems.java"));

        assertTrue(items.contains("registerItem(\"ruins_map\", MapItem::new"));
        assertTrue(items.contains("DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY"));
    }

    @Test
    void ruinsMapHasItsAssets() throws IOException {
        assertTrue(Files.exists(ModProject.ASSETS.resolve("items/ruins_map.json")));
        assertTrue(Files.exists(ModProject.ASSETS.resolve("models/item/ruins_map.json")));
        assertTrue(Files.exists(ModProject.ASSETS.resolve("textures/item/ruins_map.png")));
        assertTrue(Files.readString(ModProject.ASSETS.resolve("lang/en_us.json")).contains("\"item.graveyard.ruins_map\""));
    }

    @Test
    void ruinsMapTradeUsesTheVanillaTradeShape() throws IOException {
        String trades = Files.readString(ModProject.DATA.resolve("nameless_hanged_trades/nameless_hanged.json"));

        assertTrue(trades.contains("\"id\": \"graveyard:ruins_map\""));
        assertTrue(trades.contains("\"given_item_modifier\": ["));
        assertTrue(trades.contains("\"type\": \"minecraft:exploration_map\""));
        assertTrue(trades.contains("\"destination\": \"#graveyard:on_ruins_explorer_maps\""));
        assertTrue(trades.contains("\"decoration\": \"minecraft:ocean_ruin_warm\""));
        assertTrue(trades.contains("\"type\": \"minecraft:discard\""));
        assertFalse(trades.contains("\"structure_id\": \"graveyard:on_ruins_explorer_maps\""));
    }
}

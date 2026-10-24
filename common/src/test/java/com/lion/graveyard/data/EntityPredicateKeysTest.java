package com.lion.graveyard.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.ModProject;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityPredicateKeysTest {
    @TestFactory
    Stream<DynamicTest> advancementsUsePrefixedEntityPredicateKeys() {
        return keyTests("advancement");
    }

    @TestFactory
    Stream<DynamicTest> lootTablesUsePrefixedEntityPredicateKeys() {
        return keyTests("loot_table");
    }

    @Test
    void bareKeyInTheCurrentConditionShapeIsDetected() {
        String bare = "{\"type\":\"minecraft:entity_properties\",\"entity\":\"this\",\"predicate\":{\"type\":\"minecraft:zombie\"}}";
        String prefixed = "{\"type\":\"minecraft:entity_properties\",\"entity\":\"this\",\"predicate\":{\"minecraft:entity_type\":\"minecraft:zombie\"}}";

        assertTrue(hasBareKey(Json.parseStrict(bare)));
        assertFalse(hasBareKey(Json.parseStrict(prefixed)));
    }

    @Test
    void bareKeyInAnEntityFieldIsDetected() {
        assertTrue(hasBareKey(Json.parseStrict("{\"entity\":{\"location\":{}}}")));
        assertFalse(hasBareKey(Json.parseStrict("{\"entity\":{\"type\":\"minecraft:entity_properties\",\"entity\":\"this\"}}")));
    }

    private static Stream<DynamicTest> keyTests(String folder) {
        Path dir = ModProject.DATA.resolve(folder);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(
                        ModProject.idOf(dir, file, ".json"), () -> {
                            assertFalse(hasBareKey(Json.parseStrict(ModProject.read(file))), "entity predicate with a bare type or location key");
                        }));
    }

    private static boolean hasBareKey(JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (hasBareKey(child)) {
                    return true;
                }
            }

            return false;
        }

        if (!element.isJsonObject()) {
            return false;
        }

        JsonObject object = element.getAsJsonObject();

        if (isEntityProperties(object)
                && object.has("predicate")
                && object.get("predicate").isJsonObject()
                && isBare(object.getAsJsonObject("predicate"))) {
            return true;
        }

        if (object.has("entity")
                && object.get("entity").isJsonObject()
                && !isEntityProperties(object.getAsJsonObject("entity"))
                && isBare(object.getAsJsonObject("entity"))) {
            return true;
        }

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (hasBareKey(entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    private static boolean isEntityProperties(JsonObject object) {
        return "minecraft:entity_properties".equals(typeOf(object));
    }

    private static String typeOf(JsonObject object) {
        JsonElement type = object.has("type") ? object.get("type") : object.get("condition");
        return type != null && type.isJsonPrimitive() ? type.getAsString() : null;
    }

    private static boolean isBare(JsonObject predicate) {
        return predicate.has("type") || predicate.has("location");
    }
}

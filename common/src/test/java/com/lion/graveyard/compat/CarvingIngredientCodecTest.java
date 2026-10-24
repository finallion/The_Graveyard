package com.lion.graveyard.compat;

import com.google.gson.JsonParser;
import com.lion.graveyard.recipe.CarvingRecipe;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CarvingIngredientCodecTest {
    private static boolean componentsBound;
    private static RegistryOps<com.google.gson.JsonElement> ops;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        HolderLookup.Provider lookup = VanillaRegistries.createWorldLookup();
        ops = RegistryOps.create(JsonOps.INSTANCE, lookup);
    }

    private static void bindComponents() {
        if (!componentsBound) {
            BuiltInRegistries.ITEM.listElements().forEach(item -> item.bindComponents(DataComponentMap.EMPTY));
            componentsBound = true;
        }
    }

    private static final class TestRecipe extends CarvingRecipe {
        TestRecipe(String group, Ingredient input, ItemStackTemplate output) {
            super(null, null, group, input, output);
        }

        @Override
        public boolean matches(SingleRecipeInput input, Level level) {
            return false;
        }

        int outputCount() {
            return this.output.count();
        }
    }

    @Test
    @Order(1)
    void decodesAWholeRecipeBeforeComponentsAreBound() {
        String json = "{\"group\":\"g\",\"ingredient\":{\"item\":\"minecraft:bone_block\"},\"result\":\"minecraft:bone_meal\",\"count\":3}";
        TestRecipe recipe = CarvingRecipe.serializer(TestRecipe::new)
                .codec()
                .codec()
                .parse(ops, JsonParser.parseString(json))
                .getOrThrow();
        bindComponents();
        assertEquals(Items.BONE_MEAL, recipe.getOutputItem());
        assertEquals(3, recipe.outputCount());
    }

    private static Optional<Ingredient> parse(String json) {
        bindComponents();
        return CarvingRecipe.INGREDIENT_CODEC.parse(ops, JsonParser.parseString(json)).result();
    }

    @Test
    void readsTheStringForm() {
        Ingredient ingredient = parse("\"minecraft:bone_block\"").orElseThrow();
        assertTrue(ingredient.test(new ItemStack(Items.BONE_BLOCK)));
        assertFalse(ingredient.test(new ItemStack(Items.OAK_LOG)));
    }

    @Test
    void readsTheOldItemObject() {
        Ingredient ingredient = parse("{\"item\":\"minecraft:bone_block\"}").orElseThrow();
        assertTrue(ingredient.test(new ItemStack(Items.BONE_BLOCK)));
        assertFalse(ingredient.test(new ItemStack(Items.OAK_LOG)));
    }

    @Test
    void readsTheOldTagObject() {
        assertTrue(parse("{\"tag\":\"minecraft:logs\"}").isPresent());
    }

    @Test
    void readsAMixedList() {
        Ingredient ingredient = parse("[{\"item\":\"minecraft:bone_block\"},\"minecraft:stone\"]").orElseThrow();
        assertTrue(ingredient.test(new ItemStack(Items.BONE_BLOCK)));
        assertTrue(ingredient.test(new ItemStack(Items.STONE)));
    }

    @Test
    void rejectsAnObjectWithoutItemOrTag() {
        assertTrue(parse("{}").isEmpty());
    }
}

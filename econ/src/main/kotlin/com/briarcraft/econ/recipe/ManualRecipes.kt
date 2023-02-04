package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.recipe.ManualRecipe
import com.briarcraft.econ.api.recipe.RecipeSet
import org.bukkit.Material

class ManualRecipes: RecipeSet {
    override val recipes = setOf(
        ManualRecipe(Material.WHITE_SHULKER_BOX, Material.SHULKER_BOX, Material.WHITE_DYE),
        ManualRecipe(Material.ORANGE_SHULKER_BOX, Material.SHULKER_BOX, Material.ORANGE_DYE),
        ManualRecipe(Material.MAGENTA_SHULKER_BOX, Material.SHULKER_BOX, Material.MAGENTA_DYE),
        ManualRecipe(Material.LIGHT_BLUE_SHULKER_BOX, Material.SHULKER_BOX, Material.LIGHT_BLUE_DYE),
        ManualRecipe(Material.YELLOW_SHULKER_BOX, Material.SHULKER_BOX, Material.YELLOW_DYE),
        ManualRecipe(Material.LIME_SHULKER_BOX, Material.SHULKER_BOX, Material.LIME_DYE),
        ManualRecipe(Material.PINK_SHULKER_BOX, Material.SHULKER_BOX, Material.PINK_DYE),
        ManualRecipe(Material.GRAY_SHULKER_BOX, Material.SHULKER_BOX, Material.GRAY_DYE),
        ManualRecipe(Material.LIGHT_GRAY_SHULKER_BOX, Material.SHULKER_BOX, Material.LIGHT_GRAY_DYE),
        ManualRecipe(Material.CYAN_SHULKER_BOX, Material.SHULKER_BOX, Material.CYAN_DYE),
        ManualRecipe(Material.PURPLE_SHULKER_BOX, Material.SHULKER_BOX, Material.PURPLE_DYE),
        ManualRecipe(Material.BLUE_SHULKER_BOX, Material.SHULKER_BOX, Material.BLUE_DYE),
        ManualRecipe(Material.BROWN_SHULKER_BOX, Material.SHULKER_BOX, Material.BROWN_DYE),
        ManualRecipe(Material.GREEN_SHULKER_BOX, Material.SHULKER_BOX, Material.GREEN_DYE),
        ManualRecipe(Material.RED_SHULKER_BOX, Material.SHULKER_BOX, Material.RED_DYE),
        ManualRecipe(Material.BLACK_SHULKER_BOX, Material.SHULKER_BOX, Material.BLACK_DYE),

        ManualRecipe(Material.WHITE_CONCRETE, Material.WHITE_CONCRETE_POWDER),
        ManualRecipe(Material.ORANGE_CONCRETE, Material.ORANGE_CONCRETE_POWDER),
        ManualRecipe(Material.MAGENTA_CONCRETE, Material.MAGENTA_CONCRETE_POWDER),
        ManualRecipe(Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_CONCRETE_POWDER),
        ManualRecipe(Material.YELLOW_CONCRETE, Material.YELLOW_CONCRETE_POWDER),
        ManualRecipe(Material.LIME_CONCRETE, Material.LIME_CONCRETE_POWDER),
        ManualRecipe(Material.PINK_CONCRETE, Material.PINK_CONCRETE_POWDER),
        ManualRecipe(Material.GRAY_CONCRETE, Material.GRAY_CONCRETE_POWDER),
        ManualRecipe(Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE_POWDER),
        ManualRecipe(Material.CYAN_CONCRETE, Material.CYAN_CONCRETE_POWDER),
        ManualRecipe(Material.PURPLE_CONCRETE, Material.PURPLE_CONCRETE_POWDER),
        ManualRecipe(Material.BLUE_CONCRETE, Material.BLUE_CONCRETE_POWDER),
        ManualRecipe(Material.BROWN_CONCRETE, Material.BROWN_CONCRETE_POWDER),
        ManualRecipe(Material.GREEN_CONCRETE, Material.GREEN_CONCRETE_POWDER),
        ManualRecipe(Material.RED_CONCRETE, Material.RED_CONCRETE_POWDER),
        ManualRecipe(Material.BLACK_CONCRETE, Material.BLACK_CONCRETE_POWDER),

        ManualRecipe(Material.STRIPPED_OAK_LOG, Material.OAK_LOG),
        ManualRecipe(Material.STRIPPED_SPRUCE_LOG, Material.SPRUCE_LOG),
        ManualRecipe(Material.STRIPPED_BIRCH_LOG, Material.BIRCH_LOG),
        ManualRecipe(Material.STRIPPED_JUNGLE_LOG, Material.JUNGLE_LOG),
        ManualRecipe(Material.STRIPPED_ACACIA_LOG, Material.ACACIA_LOG),
        ManualRecipe(Material.STRIPPED_DARK_OAK_LOG, Material.DARK_OAK_LOG),
        ManualRecipe(Material.STRIPPED_MANGROVE_LOG, Material.MANGROVE_LOG),
        ManualRecipe(Material.STRIPPED_CRIMSON_STEM, Material.CRIMSON_STEM),
        ManualRecipe(Material.STRIPPED_WARPED_STEM, Material.WARPED_STEM),

        ManualRecipe(Material.CARVED_PUMPKIN, Material.PUMPKIN),

        ManualRecipe(Material.DEAD_TUBE_CORAL_BLOCK, Material.TUBE_CORAL_BLOCK),
        ManualRecipe(Material.DEAD_BRAIN_CORAL_BLOCK, Material.BRAIN_CORAL_BLOCK),
        ManualRecipe(Material.DEAD_BUBBLE_CORAL_BLOCK, Material.BUBBLE_CORAL_BLOCK),
        ManualRecipe(Material.DEAD_FIRE_CORAL_BLOCK, Material.FIRE_CORAL_BLOCK),
        ManualRecipe(Material.DEAD_HORN_CORAL_BLOCK, Material.HORN_CORAL_BLOCK),
        ManualRecipe(Material.DEAD_BRAIN_CORAL, Material.BRAIN_CORAL),
        ManualRecipe(Material.DEAD_BUBBLE_CORAL, Material.BUBBLE_CORAL),
        ManualRecipe(Material.DEAD_FIRE_CORAL, Material.FIRE_CORAL),
        ManualRecipe(Material.DEAD_HORN_CORAL, Material.HORN_CORAL),
        ManualRecipe(Material.DEAD_TUBE_CORAL, Material.TUBE_CORAL),
        ManualRecipe(Material.DEAD_TUBE_CORAL_FAN, Material.TUBE_CORAL_FAN),
        ManualRecipe(Material.DEAD_BRAIN_CORAL_FAN, Material.BRAIN_CORAL_FAN),
        ManualRecipe(Material.DEAD_BUBBLE_CORAL_FAN, Material.BUBBLE_CORAL_FAN),
        ManualRecipe(Material.DEAD_FIRE_CORAL_FAN, Material.FIRE_CORAL_FAN),
        ManualRecipe(Material.DEAD_HORN_CORAL_FAN, Material.HORN_CORAL_FAN),

        ManualRecipe(Material.COD, Material.COD_BUCKET),
        ManualRecipe(Material.SALMON, Material.SALMON_BUCKET),
        ManualRecipe(Material.TROPICAL_FISH, Material.TROPICAL_FISH_BUCKET),
        ManualRecipe(Material.PUFFERFISH, Material.PUFFERFISH_BUCKET),

        ManualRecipe(Material.MUD, Material.DIRT),

        ManualRecipe(Material.WRITTEN_BOOK, Material.WRITABLE_BOOK),
        ManualRecipe(Material.FILLED_MAP, Material.MAP),

        ManualRecipe(Material.DEEPSLATE_COAL_ORE, Material.COAL_ORE),
        ManualRecipe(Material.DEEPSLATE_IRON_ORE, Material.IRON_ORE),
        ManualRecipe(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_ORE),
        ManualRecipe(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_ORE),
        ManualRecipe(Material.DEEPSLATE_REDSTONE_ORE, Material.REDSTONE_ORE),
        ManualRecipe(Material.DEEPSLATE_EMERALD_ORE, Material.EMERALD_ORE),
        ManualRecipe(Material.DEEPSLATE_LAPIS_ORE, Material.LAPIS_ORE),
        ManualRecipe(Material.DEEPSLATE_DIAMOND_ORE, Material.DIAMOND_ORE),

        ManualRecipe(Material.EXPOSED_COPPER, Material.COPPER_BLOCK),
        ManualRecipe(Material.WEATHERED_COPPER, Material.COPPER_BLOCK),
        ManualRecipe(Material.OXIDIZED_COPPER, Material.COPPER_BLOCK),

        ManualRecipe(Material.RAW_IRON, Material.IRON_ORE),
        ManualRecipe(Material.RAW_COPPER, Material.COPPER_ORE),
        ManualRecipe(Material.RAW_GOLD, Material.GOLD_ORE),
    )
}
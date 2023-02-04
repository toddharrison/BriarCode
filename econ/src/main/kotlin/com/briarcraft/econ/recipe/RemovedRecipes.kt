package com.briarcraft.econ.recipe

import com.briarcraft.econ.api.recipe.RecipeManager
import com.briarcraft.econ.api.recipe.RecipeSet
import org.bukkit.NamespacedKey

class RemovedRecipes(recipeManager: RecipeManager): RecipeSet {
    override val recipes = setOf(
        "minecraft:iron_ingot_from_iron_block",
        "minecraft:gold_ingot_from_gold_block",
        "minecraft:copper_ingot_from_waxed_copper_block",
        "minecraft:netherite_ingot_from_netherite_block",
        "minecraft:gold_nugget_from_smelting",
        "minecraft:iron_nugget_from_smelting",
        "minecraft:gold_nugget_from_blasting",
        "minecraft:iron_nugget_from_blasting",
        "minecraft:bone_meal_from_bone_block",

        "minecraft:slime_ball",
        "minecraft:wheat",
        "minecraft:honey_bottle",

        "minecraft:raw_copper",
        "minecraft:raw_iron",
        "minecraft:raw_gold",
        "minecraft:redstone",
        "minecraft:coal",
        "minecraft:lapis_lazuli",
        "minecraft:dried_kelp",
        "minecraft:copper_ingot",
        "minecraft:diamond",
        "minecraft:emerald",
        "minecraft:map_extending",

        "minecraft:sugar_from_honey_bottle",
        "minecraft:black_dye_from_wither_rose",
        "minecraft:light_blue_dye_from_blue_white_dye",
        "minecraft:stick_from_bamboo_item",
        "minecraft:blue_dye",
        "minecraft:red_dye_from_beetroot",

        "minecraft:mossy_cobblestone_from_moss_block",
        "minecraft:mossy_stone_bricks_from_moss_block",
    )
        .map(NamespacedKey::fromString)
        .mapNotNull { recipeManager.getRecipe(it!!) }
        .toSet()
}
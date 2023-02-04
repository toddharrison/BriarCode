package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.MaterialSet
import org.bukkit.Material

class UnavailableItemMaterials: MaterialSet {
    override val types: Set<Material> = setOf(
        Material.COMMAND_BLOCK,
        Material.COMMAND_BLOCK_MINECART,
        Material.CHAIN_COMMAND_BLOCK,
        Material.REPEATING_COMMAND_BLOCK,

        Material.BARRIER,
        Material.LIGHT,
        Material.STRUCTURE_BLOCK,
        Material.STRUCTURE_VOID,
        Material.DEBUG_STICK,
        Material.KNOWLEDGE_BOOK,
        Material.JIGSAW,

        Material.TALL_GRASS,
        Material.LARGE_FERN,

        Material.BEDROCK,
        Material.BUDDING_AMETHYST,
        Material.PETRIFIED_OAK_SLAB,
        Material.SPAWNER,
        Material.FARMLAND,
        Material.DIRT_PATH,
        Material.END_PORTAL_FRAME,
        Material.REINFORCED_DEEPSLATE,

        Material.INFESTED_STONE,
        Material.INFESTED_COBBLESTONE,
        Material.INFESTED_STONE_BRICKS,
        Material.INFESTED_MOSSY_STONE_BRICKS,
        Material.INFESTED_CRACKED_STONE_BRICKS,
        Material.INFESTED_CHISELED_STONE_BRICKS,
        Material.INFESTED_DEEPSLATE,
    )
}

package com.briarcraft.rtw.util

import org.bukkit.Material
import java.util.*

val MATERIAL_ATTACHED: EnumSet<Material> = Unit.let { EnumSet.of(
    Material.SUGAR_CANE,
    Material.BAMBOO_SAPLING,
    Material.BAMBOO,
    Material.OAK_SAPLING,
    Material.BIRCH_SAPLING,
    Material.SPRUCE_SAPLING,
    Material.DARK_OAK_SAPLING,
    Material.JUNGLE_SAPLING,
    Material.ACACIA_SAPLING,
    Material.MANGROVE_PROPAGULE,
    Material.CACTUS,
    Material.GLOW_LICHEN,
    Material.SNOW,
    Material.VINE,
    Material.CAVE_VINES,
//    Material.CAVE_VINES_PLANT,
    Material.TWISTING_VINES,
//    Material.TWISTING_VINES_PLANT,
    Material.WEEPING_VINES,
//    Material.WEEPING_VINES_PLANT,
    Material.ATTACHED_MELON_STEM,
    Material.MELON_STEM,
    Material.ATTACHED_PUMPKIN_STEM,
    Material.PUMPKIN_STEM,
    Material.WHEAT,
    Material.CARROTS,
    Material.BEETROOTS,
    Material.POTATOES,
    Material.BROWN_MUSHROOM,
    Material.RED_MUSHROOM,
    Material.WARPED_FUNGUS,
    Material.CRIMSON_FUNGUS,
    Material.NETHER_WART,
    Material.CRIMSON_ROOTS,
    Material.WARPED_ROOTS,
    Material.NETHER_SPROUTS,
    Material.SWEET_BERRY_BUSH,
    Material.DEAD_BUSH,
    Material.GRASS,
    Material.TALL_GRASS,
    Material.FERN,
    Material.LARGE_FERN,
    Material.SEAGRASS,
    Material.TALL_SEAGRASS,
    Material.KELP,
    Material.KELP_PLANT,
    Material.SEA_PICKLE,
    Material.TURTLE_EGG,
    Material.CAKE,
    Material.CANDLE_CAKE,
    Material.BLACK_CANDLE_CAKE,
    Material.WHITE_CANDLE_CAKE,
    Material.GRAY_CANDLE_CAKE,
    Material.LIGHT_GRAY_CANDLE_CAKE,
    Material.BLUE_CANDLE_CAKE,
    Material.LIGHT_BLUE_CANDLE_CAKE,
    Material.GREEN_CANDLE_CAKE,
    Material.LIME_CANDLE_CAKE,
    Material.RED_CANDLE_CAKE,
    Material.PINK_CANDLE_CAKE,
    Material.MAGENTA_CANDLE_CAKE,
    Material.CYAN_CANDLE_CAKE,
    Material.BROWN_CANDLE_CAKE,
    Material.ORANGE_CANDLE_CAKE,
    Material.YELLOW_CANDLE_CAKE,
    Material.PURPLE_CANDLE_CAKE,
    Material.BRAIN_CORAL,
    Material.BUBBLE_CORAL,
    Material.FIRE_CORAL,
    Material.HORN_CORAL,
    Material.TUBE_CORAL,
    Material.BRAIN_CORAL_FAN,
    Material.BUBBLE_CORAL_FAN,
    Material.FIRE_CORAL_FAN,
    Material.HORN_CORAL_FAN,
    Material.TUBE_CORAL_FAN,
    Material.BRAIN_CORAL_WALL_FAN,
    Material.BUBBLE_CORAL_WALL_FAN,
    Material.FIRE_CORAL_WALL_FAN,
    Material.HORN_CORAL_WALL_FAN,
    Material.TUBE_CORAL_WALL_FAN,
    Material.SUNFLOWER,
    Material.PEONY,
    Material.LILAC,
    Material.ROSE_BUSH,
    Material.LILY_OF_THE_VALLEY,
    Material.CORNFLOWER,
    Material.POPPY,
    Material.AZURE_BLUET,
    Material.DANDELION,
    Material.OXEYE_DAISY,
    Material.ALLIUM,
    Material.ORANGE_TULIP,
    Material.PINK_TULIP,
    Material.RED_TULIP,
    Material.WHITE_TULIP,
    Material.BLUE_ORCHID,
    Material.AZALEA,
    Material.FLOWERING_AZALEA,
    Material.WITHER_ROSE,
    Material.LILY_PAD,
    Material.TORCH,
    Material.WALL_TORCH,
    Material.REDSTONE_TORCH,
    Material.REDSTONE_WALL_TORCH,
    Material.SOUL_TORCH,
    Material.SOUL_WALL_TORCH,
    Material.LANTERN,
    Material.SOUL_LANTERN,
    Material.OAK_BUTTON,
    Material.BIRCH_BUTTON,
    Material.SPRUCE_BUTTON,
    Material.DARK_OAK_BUTTON,
    Material.JUNGLE_BUTTON,
    Material.ACACIA_BUTTON,
    Material.CRIMSON_BUTTON,
    Material.WARPED_BUTTON,
    Material.MANGROVE_BUTTON,
    Material.STONE_BUTTON,
    Material.POLISHED_BLACKSTONE_BUTTON,
    Material.OAK_PRESSURE_PLATE,
    Material.BIRCH_PRESSURE_PLATE,
    Material.SPRUCE_PRESSURE_PLATE,
    Material.DARK_OAK_PRESSURE_PLATE,
    Material.JUNGLE_PRESSURE_PLATE,
    Material.ACACIA_PRESSURE_PLATE,
    Material.CRIMSON_PRESSURE_PLATE,
    Material.WARPED_PRESSURE_PLATE,
    Material.MANGROVE_PRESSURE_PLATE,
    Material.STONE_PRESSURE_PLATE,
    Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
    Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
    Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
    Material.BLACK_CARPET,
    Material.WHITE_CARPET,
    Material.BROWN_CARPET,
    Material.BLUE_CARPET,
    Material.GREEN_CARPET,
    Material.LIME_CARPET,
    Material.LIGHT_BLUE_CARPET,
    Material.LIGHT_GRAY_CARPET,
    Material.GRAY_CARPET,
    Material.CYAN_CARPET,
    Material.MAGENTA_CARPET,
    Material.PINK_CARPET,
    Material.PURPLE_CARPET,
    Material.ORANGE_CARPET,
    Material.RED_CARPET,
    Material.YELLOW_CARPET,
    Material.MOSS_CARPET,
    Material.OAK_SIGN,
    Material.BIRCH_SIGN,
    Material.SPRUCE_SIGN,
    Material.DARK_OAK_SIGN,
    Material.JUNGLE_SIGN,
    Material.ACACIA_SIGN,
    Material.CRIMSON_SIGN,
    Material.WARPED_SIGN,
    Material.MANGROVE_SIGN,
    Material.OAK_WALL_SIGN,
    Material.BIRCH_WALL_SIGN,
    Material.SPRUCE_WALL_SIGN,
    Material.DARK_OAK_WALL_SIGN,
    Material.JUNGLE_WALL_SIGN,
    Material.ACACIA_WALL_SIGN,
    Material.CRIMSON_WALL_SIGN,
    Material.WARPED_WALL_SIGN,
    Material.MANGROVE_WALL_SIGN,
    Material.OAK_DOOR,
    Material.BIRCH_DOOR,
    Material.SPRUCE_DOOR,
    Material.DARK_OAK_DOOR,
    Material.JUNGLE_DOOR,
    Material.ACACIA_DOOR,
    Material.CRIMSON_DOOR,
    Material.WARPED_DOOR,
    Material.MANGROVE_DOOR,
    Material.IRON_DOOR,
    Material.BLACK_BANNER,
    Material.BROWN_BANNER,
    Material.WHITE_BANNER,
    Material.BLUE_BANNER,
    Material.LIGHT_BLUE_BANNER,
    Material.GREEN_BANNER,
    Material.LIME_BANNER,
    Material.PURPLE_BANNER,
    Material.RED_BANNER,
    Material.ORANGE_BANNER,
    Material.YELLOW_BANNER,
    Material.GRAY_BANNER,
    Material.LIGHT_GRAY_BANNER,
    Material.CYAN_BANNER,
    Material.MAGENTA_BANNER,
    Material.PINK_BANNER,
    Material.BLACK_WALL_BANNER,
    Material.BROWN_WALL_BANNER,
    Material.WHITE_WALL_BANNER,
    Material.BLUE_WALL_BANNER,
    Material.LIGHT_BLUE_WALL_BANNER,
    Material.GREEN_WALL_BANNER,
    Material.LIME_WALL_BANNER,
    Material.PURPLE_WALL_BANNER,
    Material.RED_WALL_BANNER,
    Material.ORANGE_WALL_BANNER,
    Material.YELLOW_WALL_BANNER,
    Material.GRAY_WALL_BANNER,
    Material.LIGHT_GRAY_WALL_BANNER,
    Material.CYAN_WALL_BANNER,
    Material.MAGENTA_WALL_BANNER,
    Material.PINK_WALL_BANNER,
    Material.WHITE_BED,
    Material.BLACK_BED,
    Material.GRAY_BED,
    Material.LIGHT_GRAY_BED,
    Material.BLUE_BED,
    Material.LIGHT_BLUE_BED,
    Material.GREEN_BED,
    Material.LIME_BED,
    Material.ORANGE_BED,
    Material.RED_BED,
    Material.PINK_BED,
    Material.YELLOW_BED,
    Material.MAGENTA_BED,
    Material.CYAN_BED,
    Material.BROWN_BED,
    Material.PURPLE_BED,
    Material.TRIPWIRE_HOOK,
    Material.LEVER,
    Material.LADDER,
    Material.BIG_DRIPLEAF,
    Material.BIG_DRIPLEAF_STEM,
    Material.SMALL_DRIPLEAF,
    Material.POINTED_DRIPSTONE,
    Material.ITEM_FRAME,
    Material.GLOW_ITEM_FRAME,
    Material.RAIL,
    Material.DETECTOR_RAIL,
    Material.ACTIVATOR_RAIL,
    Material.POWERED_RAIL,
    Material.REDSTONE,
    Material.REDSTONE_WIRE,
    Material.PISTON_HEAD,
    Material.BELL,
    Material.PAINTING,
    Material.REPEATER,
    Material.COMPARATOR
)}
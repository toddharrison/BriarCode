package com.briarcraft.rtw.config

import org.bukkit.configuration.Configuration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class RestorerConfig(
    val griefCheckDelay: Duration = 5.minutes,
    val minAgeOfGrief: Duration = 3.hours,
    val playerCheckDelay: Duration = 5.minutes,

    val illegalToAir: Duration = 0.days,
    val perishableToAir: Duration = 1.days,
    val liquidToAir: Duration = 2.days,
    val plantToAir: Duration = 3.days,
    val soilToAir: Duration = 4.days,
    val spongyToAir: Duration = 5.days,
    val fiberToAir: Duration = 6.days,
    val woodToAir: Duration = 7.days,
    val logToAir: Duration = 9.days,
    val glassToAir: Duration = 11.days,
    val softStoneToAir: Duration = 13.days,
    val stoneToAir: Duration = 15.days,
    val hardStoneToAir: Duration = 17.days,
    val oreToAir: Duration = 20.days,
    val metalToAir: Duration = 23.days,
    val magicToAir: Duration = 26.days,
    val gemToAir: Duration = 29.days,
    val containerToAir: Duration = 30.days,

    val airToSoil: Duration = 3.days,
    val airToLog: Duration = 5.days,
    val airToWood: Duration = 7.days,
    val airToFiber: Duration = 9.days,
    val airToSpongy: Duration = 11.days,
    val airToPlant: Duration = 13.days,
    val airToSoftStone: Duration = 15.days,
    val airToStone: Duration = 17.days,
    val airToHardStone: Duration = 19.days,
    val airToGlass: Duration = 21.days,
    val airToLiquid: Duration = 23.days,
    val airToOre: Duration = 25.days,
    val airToMetal: Duration = 27.days,
    val airToGem: Duration = 29.days,
    val airToContainer: Duration = 31.days,
    val airToMagic: Duration = 33.days,
    val airToPerishable: Duration = 35.days,
)

fun loadRestorerConfig(config: Configuration): RestorerConfig {
    val griefCheckDelay = config.getInt("restorer.frequency-to-check-mob-grief-in-seconds", 300).seconds
    val minAgeOfGrief = config.getInt("restorer.mob-grief-restore-time-in-seconds", 10800).seconds
    val playerCheckDelay = config.getInt("restorer.frequency-to-check-player-changes-in-seconds", 300).seconds

    val restoreUnits = when (config.getString("restorer.restore.units")?.lowercase() ?: "days") {
        "seconds" -> { value: Int -> value.seconds }
        "minutes" -> { value: Int -> value.minutes }
        "days" -> { value: Int -> value.days }
        else -> throw IllegalStateException("Unrecognized restorer units in config")
    }

    val illegalToAir = restoreUnits(config.getInt("restorer.restore.to-air.illegal", 0))
    val perishableToAir = restoreUnits(config.getInt("restorer.restore.to-air.perishable", 1))
    val liquidToAir = restoreUnits(config.getInt("restorer.restore.to-air.liquid", 2))
    val plantToAir = restoreUnits(config.getInt("restorer.restore.to-air.plant", 3))
    val soilToAir = restoreUnits(config.getInt("restorer.restore.to-air.soil", 4))
    val spongyToAir = restoreUnits(config.getInt("restorer.restore.to-air.spongy", 5))
    val fiberToAir = restoreUnits(config.getInt("restorer.restore.to-air.fiber", 6))
    val woodToAir = restoreUnits(config.getInt("restorer.restore.to-air.wood", 7))
    val logToAir = restoreUnits(config.getInt("restorer.restore.to-air.log", 9))
    val glassToAir = restoreUnits(config.getInt("restorer.restore.to-air.glass", 11))
    val softStoneToAir = restoreUnits(config.getInt("restorer.restore.to-air.soft-stone", 13))
    val stoneToAir = restoreUnits(config.getInt("restorer.restore.to-air.stone", 15))
    val hardStoneToAir = restoreUnits(config.getInt("restorer.restore.to-air.hard-stone", 17))
    val oreToAir = restoreUnits(config.getInt("restorer.restore.to-air.ore", 20))
    val metalToAir = restoreUnits(config.getInt("restorer.restore.to-air.metal", 23))
    val magicToAir = restoreUnits(config.getInt("restorer.restore.to-air.magic", 26))
    val gemToAir = restoreUnits(config.getInt("restorer.restore.to-air.gem", 29))
    val containerToAir = restoreUnits(config.getInt("restorer.restore.to-air.container", 30))

    val airToSoil = restoreUnits(config.getInt("restorer.restore.to-material.soil", 3))
    val airToLog = restoreUnits(config.getInt("restorer.restore.to-material.log", 5))
    val airToWood = restoreUnits(config.getInt("restorer.restore.to-material.wood", 7))
    val airToFiber = restoreUnits(config.getInt("restorer.restore.to-material.fiber", 9))
    val airToSpongy = restoreUnits(config.getInt("restorer.restore.to-material.spongy", 11))
    val airToPlant = restoreUnits(config.getInt("restorer.restore.to-material.plant", 13))
    val airToSoftStone = restoreUnits(config.getInt("restorer.restore.to-material.soft-stone", 15))
    val airToStone = restoreUnits(config.getInt("restorer.restore.to-material.stone", 17))
    val airToHardStone = restoreUnits(config.getInt("restorer.restore.to-material.hard-stone", 19))
    val airToGlass = restoreUnits(config.getInt("restorer.restore.to-material.glass", 21))
    val airToLiquid = restoreUnits(config.getInt("restorer.restore.to-material.liquid", 23))
    val airToOre = restoreUnits(config.getInt("restorer.restore.to-material.ore", 25))
    val airToMetal = restoreUnits(config.getInt("restorer.restore.to-material.metal", 27))
    val airToGem = restoreUnits(config.getInt("restorer.restore.to-material.gem", 29))
    val airToContainer = restoreUnits(config.getInt("restorer.restore.to-material.container", 31))
    val airToMagic = restoreUnits(config.getInt("restorer.restore.to-material.magic", 33))
    val airToPerishable = restoreUnits(config.getInt("restorer.restore.to-material.perishable", 35))

    return RestorerConfig(
        griefCheckDelay, minAgeOfGrief, playerCheckDelay,
        illegalToAir, perishableToAir, liquidToAir, plantToAir, soilToAir, spongyToAir, fiberToAir, woodToAir, logToAir,
        glassToAir, softStoneToAir, stoneToAir, hardStoneToAir, oreToAir, metalToAir, magicToAir, gemToAir, containerToAir,
        airToSoil, airToLog, airToWood, airToFiber, airToSpongy, airToPlant, airToSoftStone, airToStone, airToHardStone,
        airToGlass, airToLiquid, airToOre, airToMetal, airToGem, airToContainer, airToMagic, airToPerishable,
    )
}

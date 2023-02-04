package com.briarcraft.regiondifficulty

import kotlin.random.Random

//const val worldName = "briar"
const val difficultyKey = "region-difficulty"

fun Random.nextBoolean(odds: Double) = nextDouble() < odds

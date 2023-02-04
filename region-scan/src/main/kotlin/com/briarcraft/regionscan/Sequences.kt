package com.briarcraft.regionscan

import com.briarcraft.kotlin.util.ChunkLocation

fun getSpiralSequence() = sequence {
    var layer = 1
    var leg = 0
    var x = 0
    var z = 0

    while (true) {
        yield(ChunkLocation(x, z))
        when (leg) {
            0 -> { x++; if (x == layer) leg++ }
            1 -> { z++; if (z == layer) leg++ }
            2 -> { x--; if (-x == layer) leg++ }
            3 -> { z--; if (-z == layer) { leg = 0; layer++ } }
        }
    }
}

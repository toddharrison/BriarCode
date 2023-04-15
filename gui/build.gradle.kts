import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "1.1.0"
description = ""

dependencies {
    api(project(":kotlin"))
}

pitest {
    mutationThreshold.set(0)
    coverageThreshold.set(0)
}

kover {
    verify {
        rule {
            bound {
                minValue = 0
                counter = kotlinx.kover.api.CounterType.INSTRUCTION
                valueType = kotlinx.kover.api.VerificationValueType.COVERED_PERCENTAGE
            }
        }
    }
}

bukkit {
    prefix = "gui"
    description = "gui plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.gui.GuiPlugin"
    depend = listOf("kotlin")
}

package com.briarcraft.econ

import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.market.MarketService
import com.briarcraft.econ.api.material.MaterialService
import com.briarcraft.econ.api.recipe.RecipeService
import com.briarcraft.econ.command.CurrencyCommand
import com.briarcraft.econ.command.MarketCommand
import com.briarcraft.econ.currency.loadCurrencyService
import com.briarcraft.econ.currency.saveCurrencyService
import com.briarcraft.econ.market.loadMarketService
import com.briarcraft.econ.market.saveMarketService
import com.briarcraft.econ.material.loadMaterialService
import com.briarcraft.econ.recipe.loadRecipeService
import com.briarcraft.econ.vault.VaultService
import com.briarcraft.gui.api.GuiService
import com.briarcraft.kotlin.util.runTaskTimerAsynchronously
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.HandlerList
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isDirectory

lateinit var plugin: Plugin
    internal set

@Suppress("unused")
class EconPlugin: SuspendingJavaPlugin() {
    init { plugin = this }

    private lateinit var currencyService: CurrencyService
    private lateinit var recipeService: RecipeService
    private lateinit var materialService: MaterialService
    private lateinit var marketService: MarketService

    private lateinit var currencyConfigFile: File
    private lateinit var currencyConfig: YamlConfiguration
    private lateinit var marketConfig: Configuration

    private lateinit var currencyCommand: CurrencyCommand
    private lateinit var marketCommand: MarketCommand

    private lateinit var saveTask: BukkitTask

    override suspend fun onLoadAsync() {
        saveDefaultConfig()

        val overwriteConfigs = false
        saveResourceFile("currencies.yml", overwriteConfigs)
        saveResourceFile("recipes.yml", overwriteConfigs)
        saveResourceFile("materials.yml", overwriteConfigs)
        saveResourceFile("markets.yml", overwriteConfigs)

        saveResourceDir("markets", overwriteConfigs)
    }

    override suspend fun onEnableAsync() {
        currencyConfigFile = File(dataFolder, "currencies.yml")
        currencyConfig = YamlConfiguration().also { it.load(currencyConfigFile) }
        marketConfig = YamlConfiguration().also { it.load(File(dataFolder, "markets.yml")) }

        val guiService = server.servicesManager.getRegistration(GuiService::class.java)?.provider
        require(guiService != null)

        // Load econ services
        currencyService = loadCurrencyService(plugin, currencyConfig)
            .also { it.registerService() }
        recipeService = loadRecipeService(plugin, YamlConfiguration()
            .also { it.load(File(dataFolder, "recipes.yml")) })
            .also { it.registerService() }
            .also { it.recipeManager.exportRecipes(File(dataFolder, "recipes.json")) }
        materialService = loadMaterialService(plugin, recipeService, YamlConfiguration()
            .also { it.load(File(dataFolder, "materials.yml")) })
            .also { it.registerService() }
//            .also { println(it.baseItems.types) }
        marketService = loadMarketService(plugin, currencyService, recipeService, materialService, guiService, marketConfig)
            .also { it.registerService() }
        server.pluginManager.getPlugin("Vault")?.let {
            VaultService(plugin, currencyService).registerService()
        }

        // Register commands
        currencyCommand = CurrencyCommand(currencyService).also { it.registerCommands() }
        marketCommand = MarketCommand(marketService).also { it.registerCommands() }

        // Configure auto save task, every 10 minutes
        val freq = (10 * 60 * 20).toLong()
        saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, delay = freq, period = freq) { ->
            saveCurrencyService(currencyService, currencyConfig, currencyConfigFile)
            saveMarketService(plugin, marketService)
            saveConfig()
            logger.info("Saved currency accounts")
        }
    }

    override suspend fun onDisableAsync() {
        // Cancel save task
        saveTask.cancel()

        // Unregister commands
        currencyCommand.unregisterCommands()
        marketCommand.unregisterCommands()

        // Unregister services and listeners
        server.servicesManager.unregisterAll(plugin)
        HandlerList.unregisterAll(plugin)

        // Save
        saveCurrencyService(currencyService, currencyConfig, currencyConfigFile)
        saveMarketService(plugin, marketService)
        saveConfig()
    }



    @Suppress("SameParameterValue")
    private fun saveResourceFile(resourceFile: String, replace: Boolean) {
        if (!replace && File(plugin.dataFolder, resourceFile).exists()) {
            return
        }
        saveResource(resourceFile, replace)
    }

    private suspend fun saveResourceDir(resourceDir: String, replace: Boolean) {
        if (replace) {
            check(File(plugin.dataFolder, resourceDir).deleteRecursively())
        } else if (File(plugin.dataFolder, resourceDir).exists()) {
            return
        }
        withContext(Dispatchers.IO) {
            val uri = classLoader.getResource(resourceDir)!!.toURI()
            val env = mapOf("create" to "true")
            FileSystems.newFileSystem(uri, env)
            Files.walk(Paths.get(uri)).forEach { path ->
                if (!path.isDirectory()) {
                    val resource = path.toString().removePrefix(File.separator)
                    plugin.saveResource(resource, replace)
                }
            }
        }
    }
}

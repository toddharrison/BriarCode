package com.briarcraft.econ.vault

import com.briarcraft.econ.api.currency.CurrencyService
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority

class VaultService(
    private val plugin: Plugin,
    private val currencyService: CurrencyService
): Economy {
    fun registerService() =
        plugin.server.servicesManager.let { servicesManager ->
            if (!servicesManager.isProvidedFor(Economy::class.java)) {
                servicesManager.register(Economy::class.java, this, plugin, ServicePriority.Normal)
                true
            } else false
        }

    override fun isEnabled() = true
    override fun getName() = "EconApi"
    override fun fractionalDigits() = currencyService.defaultCurrency.format.maximumFractionDigits
    override fun format(value: Double): String = currencyService.defaultCurrency.format.format(value)

    override fun currencyNameSingular() = currencyService.defaultCurrency.name
    override fun currencyNamePlural() = currencyService.defaultCurrency.namePlural

    @Deprecated("Deprecated in Java")
    override fun hasAccount(playerName: String?) = hasAccount(getPlayerByName(playerName))
    override fun hasAccount(player: OfflinePlayer?) = hasAccount(player, currencyService.defaultCurrency.name)
    @Deprecated("Deprecated in Java")
    override fun hasAccount(playerName: String?, currencyName: String?) = hasAccount(getPlayerByName(playerName), currencyName)
    override fun hasAccount(player: OfflinePlayer?, currencyName: String?) = player != null

    @Deprecated("Deprecated in Java")
    override fun createPlayerAccount(playerName: String?) = createPlayerAccount(getPlayerByName(playerName))
    override fun createPlayerAccount(player: OfflinePlayer?) = createPlayerAccount(player, currencyService.defaultCurrency.name)
    @Deprecated("Deprecated in Java")
    override fun createPlayerAccount(playerName: String?, currencyName: String?) = createPlayerAccount(getPlayerByName(playerName), currencyName)
    override fun createPlayerAccount(player: OfflinePlayer?, currencyName: String?) = false

    @Deprecated("Deprecated in Java")
    override fun getBalance(playerName: String?) = getBalance(getPlayerByName(playerName))
    override fun getBalance(player: OfflinePlayer?) = getBalance(player, currencyService.defaultCurrency.name)
    @Deprecated("Deprecated in Java")
    override fun getBalance(playerName: String?, currencyName: String?) = getBalance(getPlayerByName(playerName), currencyName)
    override fun getBalance(player: OfflinePlayer?, currencyName: String?) =
        player?.let {
            val wallet = currencyService.getWallet(player)
            currencyService.currencies[currencyName]?.let { currency ->
                wallet.get(currency)
            }
        } ?: 0.0

    @Deprecated("Deprecated in Java")
    override fun has(playerName: String?, amount: Double) = has(getPlayerByName(playerName), amount)
    override fun has(player: OfflinePlayer?, amount: Double) = has(player, currencyService.defaultCurrency.name, amount)
    @Deprecated("Deprecated in Java")
    override fun has(playerName: String?, currencyName: String?, amount: Double) = has(getPlayerByName(playerName), currencyName, amount)
    override fun has(player: OfflinePlayer?, currencyName: String?, amount: Double) = getBalance(player, currencyName) >= amount

    @Deprecated("Deprecated in Java")
    override fun withdrawPlayer(playerName: String?, amount: Double) = withdrawPlayer(getPlayerByName(playerName), amount)
    override fun withdrawPlayer(player: OfflinePlayer?, amount: Double) = withdrawPlayer(player, currencyService.defaultCurrency.name, amount)
    @Deprecated("Deprecated in Java")
    override fun withdrawPlayer(playerName: String?, currencyName: String?, amount: Double) = withdrawPlayer(getPlayerByName(playerName), currencyName, amount)
    override fun withdrawPlayer(player: OfflinePlayer?, currencyName: String?, amount: Double) =
        player?.let {
            val wallet = currencyService.getWallet(player)
            currencyService.currencies[currencyName]?.let { currency ->
                if (wallet.remove(currency.createMoney(amount), "Removed by Vault")) {
                    val balance = wallet.get(currency) ?: 0.0
                    EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null)
                } else {
                    val balance = wallet.get(currency) ?: 0.0
                    EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, "Not enough money available")
                }
            } ?: EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "That currency does not exist")
        } ?: EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Player does not exist")

    @Deprecated("Deprecated in Java")
    override fun depositPlayer(playerName: String?, amount: Double) = depositPlayer(getPlayerByName(playerName), amount)
    override fun depositPlayer(player: OfflinePlayer?, amount: Double) = depositPlayer(player, currencyService.defaultCurrency.name, amount)
    @Deprecated("Deprecated in Java")
    override fun depositPlayer(playerName: String?, currencyName: String?, amount: Double) = depositPlayer(getPlayerByName(playerName), currencyName, amount)
    override fun depositPlayer(player: OfflinePlayer?, currencyName: String?, amount: Double) =
        player?.let {
            val wallet = currencyService.getWallet(player)
            currencyService.currencies[currencyName]?.let { currency ->
                wallet.add(currency.createMoney(amount), "Added by Vault")
                val balance = wallet.get(currency) ?: 0.0
                EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null)
            } ?: EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "That currency does not exist")
        } ?: EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Player does not exist")

    // TODO Bank support
    override fun hasBankSupport() = false
    override fun getBanks(): MutableList<String> {
        throw UnsupportedOperationException()
    }
    @Deprecated("Deprecated in Java")
    override fun createBank(bankName: String?, ownerName: String?): EconomyResponse {
        throw UnsupportedOperationException()
    }
    override fun createBank(bankName: String?, owner: OfflinePlayer?): EconomyResponse {
        throw UnsupportedOperationException()
    }
    override fun deleteBank(bankName: String?): EconomyResponse {
        throw UnsupportedOperationException()
    }
    @Deprecated("Deprecated in Java")
    override fun isBankOwner(bankName: String?, playerName: String?): EconomyResponse {
        throw UnsupportedOperationException()
    }
    override fun isBankOwner(bankName: String?, player: OfflinePlayer?): EconomyResponse {
        throw UnsupportedOperationException()
    }
    @Deprecated("Deprecated in Java")
    override fun isBankMember(bankName: String?, playerName: String?): EconomyResponse {
        throw UnsupportedOperationException()
    }
    override fun isBankMember(bankName: String?, player: OfflinePlayer?): EconomyResponse {
        throw UnsupportedOperationException()
    }
    override fun bankBalance(bankName: String?): EconomyResponse {
        throw UnsupportedOperationException()
    }
    override fun bankHas(bankName: String?, amount: Double): EconomyResponse {
        throw UnsupportedOperationException()
    }
    override fun bankDeposit(bankName: String?, amount: Double): EconomyResponse {
        throw UnsupportedOperationException()
    }
    override fun bankWithdraw(bankName: String?, amount: Double): EconomyResponse {
        throw UnsupportedOperationException()
    }

    private fun getPlayerByName(playerName: String?) =
        if (playerName != null) plugin.server.getPlayer(playerName) ?: plugin.server.getOfflinePlayerIfCached(playerName) else null
}

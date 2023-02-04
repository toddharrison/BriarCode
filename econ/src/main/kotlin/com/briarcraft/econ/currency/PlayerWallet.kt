package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.Money
import com.briarcraft.econ.api.currency.Transaction
import com.briarcraft.econ.api.currency.Wallet
import org.bukkit.OfflinePlayer
import java.time.Instant

class PlayerWallet(
    override val owner: OfflinePlayer
): Wallet {
    override val monies: MutableMap<Currency, Double> = mutableMapOf()

    private val transactions = mutableListOf<Transaction>()

    override fun get(currency: Currency) = monies[currency]

    override fun add(money: Money, description: String?): Boolean {
        monies[money.currency] = (monies[money.currency] ?: 0.0) + money.amount
        val balance = monies[money.currency]
        transactions.add(TransactionImpl(Instant.now(), money, balance, description))
        return true
    }

    override fun remove(currency: Currency, description: String?): Double? {
        val amount = monies.remove(currency)
        if (amount != null) {
            transactions.add(TransactionImpl(Instant.now(), currency.createMoney(amount), null, description))
        }
        return amount
    }

    override fun remove(money: Money, description: String?): Boolean {
        val currentAmount = monies[money.currency] ?: 0.0
        return if (currentAmount > money.amount) {
            monies[money.currency] = currentAmount - money.amount
            transactions.add(TransactionImpl(Instant.now(), money, monies[money.currency], description))
            true
        } else if (currentAmount == money.amount) {
            monies.remove(money.currency)
            transactions.add(TransactionImpl(Instant.now(), money, null, description))
            true
        } else false
    }

    override fun removeUpTo(money: Money, description: String?): Money? {
        val currentAmount = monies[money.currency] ?: 0.0
        return if (currentAmount == 0.0) money
        else if (currentAmount < money.amount) {
            monies.remove(money.currency)
            val leftOverAmount = money.amount - currentAmount
            transactions.add(TransactionImpl(Instant.now(), money.minus(leftOverAmount), null, description))
            money.currency.createMoney(leftOverAmount)
        } else {
            monies[money.currency] = currentAmount - money.amount
            transactions.add(TransactionImpl(Instant.now(), money, monies[money.currency], description))
            null
        }
    }

    override fun history() = transactions
}

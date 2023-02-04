package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.Money
import com.briarcraft.econ.api.currency.Transaction
import com.briarcraft.econ.api.currency.Wallet
import com.briarcraft.kotlin.util.addItemAtomic
import com.briarcraft.kotlin.util.removeItemAtomic
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Instant

class PlayerInventoryWallet(
    override val owner: Player
): Wallet {
    override val monies: Map<Currency, Double>
        get() {
            return mapOf()
        }

    private val transactions = mutableListOf<Transaction>()

    override fun get(currency: Currency): Double {
        require(currency is PhysicalCurrency)
        return owner.inventory
            .filter { it.type == currency.type }
            .sumOf(ItemStack::getAmount)
            .toDouble()
    }

    override fun add(money: Money, description: String?): Boolean {
        require(money is PhysicalMoney)
        val removed = owner.inventory
            .addItemAtomic(ItemStack(money.currency.type, money.quantity))
        if (removed) {
            // TODO Move to common lib
            val balance = owner.inventory.contents
                .filter { it?.type == money.currency.type }
                .filterNotNull()
                .map(ItemStack::getAmount)
                .sum()
                .toDouble()
            transactions.add(TransactionImpl(Instant.now(), money, balance, description))
        }
        return removed
    }

    override fun remove(currency: Currency, description: String?): Double? {
        require(currency is PhysicalCurrency)
        val quantity = owner.inventory
            .filter { it.type == currency.type }
            .sumOf(ItemStack::getAmount)
        return if (owner.inventory.removeItemAtomic(ItemStack(currency.type, quantity))) {
            transactions.add(TransactionImpl(Instant.now(), currency.createMoney(quantity), null, description))
            quantity.toDouble()
        } else null
    }

    override fun remove(money: Money, description: String?): Boolean {
        require(money is PhysicalMoney)
        val removed = owner.inventory.removeItemAtomic(ItemStack(money.currency.type, money.quantity))
        if (removed) {
            // TODO Move to common lib
            val balance = owner.inventory.contents
                .filter { it?.type == money.currency.type }
                .filterNotNull()
                .map(ItemStack::getAmount)
                .sum()
                .toDouble()
            transactions.add(TransactionImpl(Instant.now(), money, balance, description))
        }
        return removed
    }

    override fun removeUpTo(money: Money, description: String?): Money? {
        require(money is PhysicalMoney)
        val remainder = owner.inventory.removeItem(ItemStack(money.currency.type, money.quantity))
        return if (remainder.isEmpty()) {
            null
        } else {
            check(remainder.size == 1)
            val r = remainder[0]!!
            check(r.type == money.currency.type)
            val moneyRemoved = money.currency.createMoney(money.quantity - r.amount)
            // TODO Move to common lib
            val balance = owner.inventory.contents
                .filter { it?.type == money.currency.type }
                .filterNotNull()
                .map(ItemStack::getAmount)
                .sum()
                .toDouble()
            transactions.add(TransactionImpl(Instant.now(), moneyRemoved, balance, description))
            PhysicalMoney(money.currency, r.amount, money.currency.type)
        }
    }

    override fun history() = transactions
}

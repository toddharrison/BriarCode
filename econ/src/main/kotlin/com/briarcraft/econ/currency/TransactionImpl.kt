package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Money
import com.briarcraft.econ.api.currency.Transaction
import java.time.Instant

class TransactionImpl(
    override val time: Instant,
    override val money: Money,
    override val balance: Double?,
    override val description: String?
) : Transaction {
}

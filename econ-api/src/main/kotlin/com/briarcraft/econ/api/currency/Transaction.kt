package com.briarcraft.econ.api.currency

import java.time.Instant

interface Transaction {
    val time: Instant
    val money: Money
    val balance: Double?
    val description: String?
}

package com.briarcraft.rtw.util

import java.util.concurrent.atomic.AtomicBoolean

class AtomicToggle(initialValue: Boolean = false): AtomicBoolean(initialValue) {
    fun toggle(): Boolean {
        var temp: Boolean
        do {
            temp = get()
        } while (!compareAndSet(temp, !temp))
        return !temp
    }
}

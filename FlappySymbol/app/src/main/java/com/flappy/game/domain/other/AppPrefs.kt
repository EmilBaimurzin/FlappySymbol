package com.flappy.game.domain.other

import android.content.Context

class AppPrefs(context: Context) {
    private val sp = context.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)

    fun getBalance(): Int = sp.getInt("BALANCE", 0)
    fun increaseBalance(value: Int) = sp.edit().putInt("BALANCE", getBalance() + value).apply()

    fun isSymbolBought(value: String): Boolean = sp.getBoolean(value, false)
    fun buySymbol(value: String) = sp.edit().putBoolean(value, true).apply()

    fun getCurrentSymbol(): Int = sp.getInt("CURRENT", 1)
    fun setCurrentSymbol(id: Int) = sp.edit().putInt("CURRENT", id).apply()

}
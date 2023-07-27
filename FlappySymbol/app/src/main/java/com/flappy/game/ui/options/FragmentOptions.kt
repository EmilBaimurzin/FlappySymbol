package com.flappy.game.ui.options

import android.os.Bundle
import android.view.View
import com.flappy.game.core.library.shortToast
import com.flappy.game.databinding.FragmentOptionsBinding
import com.flappy.game.domain.other.AppPrefs
import com.flappy.game.ui.other.ViewBindingFragment

class FragmentOptions :
    ViewBindingFragment<FragmentOptionsBinding>(FragmentOptionsBinding::inflate) {
    private val sharedPrefs by lazy {
        AppPrefs(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtons()
        setBalance()
        binding.symbol1Button.setOnClickListener {
            if (sharedPrefs.isSymbolBought("1")) sharedPrefs.setCurrentSymbol(
                1
            ) else buySymbol(1)
        }
        binding.symbol2Button.setOnClickListener {
            if (sharedPrefs.isSymbolBought("2")) sharedPrefs.setCurrentSymbol(
                2
            ) else buySymbol(2)
        }
        binding.symbol3Button.setOnClickListener {
            if (sharedPrefs.isSymbolBought("3")) sharedPrefs.setCurrentSymbol(
                3
            ) else buySymbol(3)
        }
        binding.symbol4Button.setOnClickListener {
            if (sharedPrefs.isSymbolBought("4")) sharedPrefs.setCurrentSymbol(
                4
            ) else buySymbol(4)
        }
        binding.symbol5Button.setOnClickListener {
            if (sharedPrefs.isSymbolBought("5")) sharedPrefs.setCurrentSymbol(
                5
            ) else buySymbol(5)
        }
        binding.symbol6Button.setOnClickListener {
            if (sharedPrefs.isSymbolBought("6")) sharedPrefs.setCurrentSymbol(
                6
            ) else buySymbol(6)
        }
        binding.symbol7Button.setOnClickListener {
            if (sharedPrefs.isSymbolBought("7")) sharedPrefs.setCurrentSymbol(
                7
            ) else buySymbol(7)
        }
        binding.symbol8Button.setOnClickListener {
            if (sharedPrefs.isSymbolBought("8")) sharedPrefs.setCurrentSymbol(
                8
            ) else buySymbol(8)
        }
    }

    private fun buySymbol(id: Int) {
        if (id <= 4) {
            if (sharedPrefs.getBalance() >= 1000) {
                sharedPrefs.increaseBalance(-1000)
                sharedPrefs.buySymbol(id.toString())
            } else {
                shortToast(requireContext(), "Not enough coins")
            }
        } else {
            if (sharedPrefs.getBalance() >= 10000) {
                sharedPrefs.increaseBalance(-10000)
                sharedPrefs.buySymbol(id.toString())
            } else {
                shortToast(requireContext(), "Not enough coins")
            }
        }
        setButtons()
        setBalance()
    }

    private fun setBalance() {
        binding.balanceTextView.text = sharedPrefs.getBalance().toString()
    }

    private fun setButtons() {
        binding.symbol1Button.text = if (sharedPrefs.isSymbolBought("1")) "select" else "1000"
        binding.symbol2Button.text = if (sharedPrefs.isSymbolBought("2")) "select" else "1000"
        binding.symbol3Button.text = if (sharedPrefs.isSymbolBought("3")) "select" else "1000"
        binding.symbol4Button.text = if (sharedPrefs.isSymbolBought("4")) "select" else "1000"
        binding.symbol5Button.text = if (sharedPrefs.isSymbolBought("5")) "select" else "10000"
        binding.symbol6Button.text = if (sharedPrefs.isSymbolBought("6")) "select" else "10000"
        binding.symbol7Button.text = if (sharedPrefs.isSymbolBought("7")) "select" else "10000"
        binding.symbol8Button.text = if (sharedPrefs.isSymbolBought("8")) "select" else "10000"
    }
}
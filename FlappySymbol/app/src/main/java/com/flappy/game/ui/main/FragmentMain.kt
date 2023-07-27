package com.flappy.game.ui.main

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.flappy.game.R
import com.flappy.game.databinding.FragmentMainBinding
import com.flappy.game.domain.other.AppPrefs
import com.flappy.game.ui.other.ViewBindingFragment

class FragmentMain : ViewBindingFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private val sharedPrefs by lazy {
        AppPrefs(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefs.buySymbol("1")
        binding.apply {
            buttonStart.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentGame)
            }
            buttonOptions.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentOptions)
            }
            buttonExit.setOnClickListener {
                requireActivity().finish()
            }
        }
        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}
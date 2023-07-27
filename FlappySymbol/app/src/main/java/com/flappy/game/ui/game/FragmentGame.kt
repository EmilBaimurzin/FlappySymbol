package com.flappy.game.ui.game

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flappy.game.R
import com.flappy.game.core.library.l
import com.flappy.game.databinding.FragmentGameBinding
import com.flappy.game.domain.other.AppPrefs
import com.flappy.game.ui.other.ViewBindingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentGame : ViewBindingFragment<FragmentGameBinding>(FragmentGameBinding::inflate) {
    private val viewModel: GameViewModel by viewModels()
    private val callbackViewModel: CallbackViewModel by activityViewModels()
    private val sp by lazy { AppPrefs(requireContext()) }
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }
    private var symbol = R.drawable.symbol01
    private var moveScope = CoroutineScope(Dispatchers.Default)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTouchListener()
        setBalance()

        viewModel.coinCallback = {
            sp.increaseBalance(1)
            setBalance()
        }

        callbackViewModel.callback = {
            viewModel.pauseState = false
            setTouchListener()
            viewModel.start(
                dpToPx(250),
                200,
                xy.second,
                xy.first,
                dpToPx(80),
                dpToPx(50),
                dpToPx(30)
            )
        }

        symbol = when (sp.getCurrentSymbol()) {
            1 -> R.drawable.symbol01
            2 -> R.drawable.symbol02
            3 -> R.drawable.symbol03
            4 -> R.drawable.symbol04
            5 -> R.drawable.symbol05
            6 -> R.drawable.symbol06
            7 -> R.drawable.symbol07
            else -> R.drawable.symbol08
        }
        binding.playerView.setImageResource(symbol)
        lifecycleScope.launch {
            delay(20)
            if (viewModel.playerXY.value!! == 0f to 0f) {
                viewModel.initPlayer(dpToPx(60).toFloat(), (xy.second / 2 - dpToPx(40)).toFloat())
            }

            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(
                    dpToPx(250),
                    200,
                    xy.second,
                    xy.first,
                    dpToPx(80),
                    dpToPx(50),
                    dpToPx(30)
                )
            }
        }

        binding.buttonPause.setOnClickListener {
            moveScope.cancel()
            findNavController().navigate(R.id.action_fragmentGame_to_dialogPause)
            viewModel.pauseState = true
            viewModel.stop()
        }

        binding.buttonHome.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.columns.observe(viewLifecycleOwner) { columns ->
            l(columns.toString())
            binding.columnsLayout.removeAllViews()
            columns.forEach { column ->
                val columnView = ImageView(requireContext())
                columnView.layoutParams = ViewGroup.LayoutParams(dpToPx(50), dpToPx(250))
                columnView.setBackgroundResource(R.drawable.bg_column)
                columnView.rotation = if (column.isTop) 0f else 180f
                columnView.x = column.position.first
                columnView.y = column.position.second
                binding.columnsLayout.addView(columnView)
            }
        }

        viewModel.flyingLives.observe(viewLifecycleOwner) {
            binding.flyingLivesLayout.removeAllViews()
            it.forEach { live ->
                val liveView = ImageView(requireContext())
                liveView.layoutParams = ViewGroup.LayoutParams(dpToPx(30), dpToPx(30))
                liveView.setImageResource(R.drawable.img_flying_heart)
                liveView.x = live.first
                liveView.y = live.second
                binding.flyingLivesLayout.addView(liveView)
            }
        }

        viewModel.coins.observe(viewLifecycleOwner) {
            binding.coinsLayout.removeAllViews()
            it.forEach { coin ->
                val coinView = ImageView(requireContext())
                coinView.layoutParams = ViewGroup.LayoutParams(dpToPx(30), dpToPx(30))
                coinView.setImageResource(R.drawable.img_coin)
                coinView.x = coin.first
                coinView.y = coin.second
                binding.coinsLayout.addView(coinView)
            }
        }

        viewModel.lives.observe(viewLifecycleOwner) {
            binding.livesLayout.removeAllViews()
            repeat(it) {
                val liveView = ImageView(requireContext())
                liveView.layoutParams = LinearLayout.LayoutParams(dpToPx(25), dpToPx(25)).apply {
                    marginStart = dpToPx(3)
                    marginEnd = dpToPx(3)
                }
                liveView.setImageResource(R.drawable.img_heart)
                binding.livesLayout.addView(liveView)
            }
            if (viewModel.gameState && !viewModel.pauseState && it == 0) {
                end()
            }
        }

        viewModel.distance.observe(viewLifecycleOwner) {
            binding.metersTextView.text = "$it M"
        }



        viewModel.playerXY.observe(viewLifecycleOwner) {
            binding.playerView.x = it.first
            binding.playerView.y = it.second
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setTouchListener() {
        binding.root.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.movePlayerUp()
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.movePlayerUp()
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.movePlayerDown(xy.second - dpToPx(70))
                            delay(2)
                        }
                    }
                    false
                }
            }
        }
    }

    private fun end() {
        moveScope.cancel()
        viewModel.gameState = false
        viewModel.stop()
        findNavController().navigate(FragmentGameDirections.actionFragmentGameToDialogEnd(viewModel.distance.value!!))
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    private fun setBalance() {
        binding.balanceTextView.text = sp.getBalance().toString()
    }


    override fun onPause() {
        super.onPause()
        moveScope.cancel()
        viewModel.stop()
        binding.root.setOnTouchListener { _, _ ->
            false
        }
    }
}
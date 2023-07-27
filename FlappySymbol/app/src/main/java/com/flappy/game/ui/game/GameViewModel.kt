package com.flappy.game.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flappy.game.domain.game.Column
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private var gameScope = CoroutineScope(Dispatchers.Default)
    var gameState = true
    var pauseState = false
    var coinCallback: (() -> Unit)? = null
    private val _playerXY = MutableLiveData(0f to 0f)
    val playerXY: LiveData<Pair<Float, Float>> = _playerXY

    private val _columns = MutableLiveData<List<Column>>(emptyList())
    val columns: LiveData<List<Column>> = _columns

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _flyingLives = MutableLiveData<List<Pair<Float, Float>>>(emptyList())
    val flyingLives: LiveData<List<Pair<Float, Float>>> = _flyingLives

    private val _coins = MutableLiveData<List<Pair<Float, Float>>>(emptyList())
    val coins: LiveData<List<Pair<Float, Float>>> = _coins

    private val _distance = MutableLiveData(0)
    val distance: LiveData<Int> = _distance

    fun start(
        columnHeight: Int,
        columnMax: Int,
        parentY: Int,
        parentX: Int,
        playerSize: Int,
        columnWidth: Int,
        liveSize: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.Default)
        generateColumns(columnHeight, columnMax, parentY, parentX)
        letColumnsMove(playerSize, columnWidth, columnHeight)
        calculateDistance()
        generateLives(parentY - liveSize, parentX.toFloat())
        letLivesMove(playerSize, liveSize)
        generateCoins(parentY - liveSize, parentX.toFloat())
        letCoinsMove(playerSize, liveSize)
    }

    fun stop() {
        gameScope.cancel()
    }

    private fun increaseLives() {
        if (_lives.value!! + 1 <= 3) {
            _lives.postValue(_lives.value!! + 1)
        }
    }

    private fun calculateDistance() {
        gameScope.launch {
            while (true) {
                delay(1000)
                _distance.postValue(_distance.value!! + 1)
            }
        }
    }

    private fun decreaseLives() {
        if (_lives.value!! - 1 >= 0) {
            _lives.postValue(_lives.value!! - 1)
        }
    }

    fun movePlayerUp() {
        val xy = _playerXY.value!!
        if (xy.second - 2 > 0) {
            _playerXY.postValue(xy.first to xy.second - 2)
        }
    }

    fun movePlayerDown(limit: Int) {
        val xy = _playerXY.value!!
        if (xy.second + 2 < limit) {
            _playerXY.postValue(xy.first to xy.second + 2)
        }
    }

    fun initPlayer(x: Float, y: Float) {
        _playerXY.postValue(x to y)
    }

    private fun generateLives(maxY: Int, x: Float) {
        gameScope.launch {
            while (true) {
                delay(40000)
                val live = x to (0..maxY).random().toFloat()
                val newList = _flyingLives.value!!.toMutableList()
                newList.add(live)
                _flyingLives.postValue(newList)
            }
        }
    }

    private fun letLivesMove(playerSize: Int, liveSize: Int) {
        gameScope.launch {
            while (true) {
                val newList = mutableListOf<Pair<Float, Float>>()
                _flyingLives.value!!.forEachIndexed { _, live ->
                    val rangeXPLayer =
                        playerXY.value!!.first.toInt()..playerXY.value!!.first.toInt() + playerSize
                    val rangeXColumn =
                        live.first.toInt()..live.first.toInt() + liveSize

                    val rangeYPlayer =
                        playerXY.value!!.second.toInt()..playerXY.value!!.second.toInt() + playerSize
                    val rangeYColumn =
                        live.second.toInt()..live.second.toInt() + liveSize

                    val isIntersectingX = rangeXPLayer.any { it in rangeXColumn }
                    val isIntersectingY = rangeYPlayer.any { it in rangeYColumn }
                    if ((isIntersectingX && isIntersectingY) || (live.first + liveSize) - 5 < 0) {
                        if (isIntersectingX && isIntersectingY) {
                            increaseLives()
                        }
                    } else {
                        newList.add(
                            live.first - 5 to live.second
                        )
                    }
                }
                _flyingLives.postValue(newList)
                delay(5)
            }
        }
    }

    private fun generateCoins(maxY: Int, x: Float) {
        gameScope.launch {
            while (true) {
                delay(2000)
                val live = x to (0..maxY).random().toFloat()
                val newList = _coins.value!!.toMutableList()
                newList.add(live)
                _coins.postValue(newList)
            }
        }
    }

    private fun letCoinsMove(playerSize: Int, liveSize: Int) {
        gameScope.launch {
            while (true) {
                val newList = mutableListOf<Pair<Float, Float>>()
                _coins.value!!.forEachIndexed { _, coin ->
                    val rangeXPLayer =
                        playerXY.value!!.first.toInt()..playerXY.value!!.first.toInt() + playerSize
                    val rangeXColumn =
                        coin.first.toInt()..coin.first.toInt() + liveSize

                    val rangeYPlayer =
                        playerXY.value!!.second.toInt()..playerXY.value!!.second.toInt() + playerSize
                    val rangeYColumn =
                        coin.second.toInt()..coin.second.toInt() + liveSize

                    val isIntersectingX = rangeXPLayer.any { it in rangeXColumn }
                    val isIntersectingY = rangeYPlayer.any { it in rangeYColumn }
                    if ((isIntersectingX && isIntersectingY) || (coin.first + liveSize) - 5 < 0) {
                        if (isIntersectingX && isIntersectingY) {
                            coinCallback?.invoke()
                        }
                    } else {
                        newList.add(
                            coin.first - 5 to coin.second
                        )
                    }
                }
                _coins.postValue(newList)
                delay(5)
            }
        }
    }


    private fun generateColumns(columnHeight: Int, columnMax: Int, parentY: Int, parentX: Int) {
        gameScope.launch {
            while (true) {
                delay(3000)
                addColumn(true, columnHeight, columnMax, parentY, parentX)
                delay(3000)
                addColumn(false, columnHeight, columnMax, parentY, parentX)
            }
        }
    }

    private fun addColumn(
        isTop: Boolean,
        columnHeight: Int,
        columnMax: Int,
        parentY: Int,
        parentX: Int
    ) {
        val currentList = _columns.value!!.toMutableList()
        if (isTop) {
            val columnY = 0 - (columnMax / 5..columnMax + (columnMax)).random()
            currentList.add(
                Column(
                    parentX.toFloat() to columnY.toFloat(),
                    true
                )
            )
            _columns.postValue(currentList)
        } else {
            val columnY = (parentY - columnHeight) + (columnMax / 5..columnMax + (columnMax)).random()
            currentList.add(
                Column(
                    parentX.toFloat() to columnY.toFloat(),
                    false
                )
            )
            _columns.postValue(currentList)
        }
    }

    private fun letColumnsMove(playerSize: Int, columnWidth: Int, columnHeight: Int) {
        gameScope.launch {
            while (true) {
                val newList = mutableListOf<Column>()
                _columns.value!!.forEachIndexed { index, column ->
                    val rangeXPLayer =
                        playerXY.value!!.first.toInt()..playerXY.value!!.first.toInt() + playerSize
                    val rangeXColumn =
                        column.position.first.toInt()..column.position.first.toInt() + columnWidth

                    val rangeYPlayer =
                        playerXY.value!!.second.toInt()..playerXY.value!!.second.toInt() + playerSize
                    val rangeYColumn =
                        column.position.second.toInt()..column.position.second.toInt() + columnHeight

                    val isIntersectingX = rangeXPLayer.any { it in rangeXColumn }
                    val isIntersectingY = rangeYPlayer.any { it in rangeYColumn }
                    if ((isIntersectingX && isIntersectingY) || (column.position.first + columnWidth) - 5 < 0) {
                        if (isIntersectingX && isIntersectingY) {
                            decreaseLives()
                        }
                    } else {
                        newList.add(
                            Column(
                                column.position.first - 3 to column.position.second,
                                column.isTop
                            )
                        )
                    }
                }
                _columns.postValue(newList)
                delay(10)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameScope.cancel()
    }
}
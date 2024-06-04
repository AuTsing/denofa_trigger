package com.autsing.denofatrigger.watch.tile

import com.autsing.denofatrigger.watch.presentation.Step

data class MainTileState(
    val steps: List<Step>,
    val index: Int,
)

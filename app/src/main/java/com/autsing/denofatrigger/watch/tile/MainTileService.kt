package com.autsing.denofatrigger.watch.tile

import androidx.lifecycle.lifecycleScope
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.autsing.denofatrigger.watch.presentation.StepUtil
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val RESOURCES_VERSION = "0"

@OptIn(ExperimentalHorologistApi::class)
class MainTileService : SuspendingTileService() {
    private lateinit var renderer: MainTileRenderer
    private lateinit var state: StateFlow<MainTileState?>

    override fun onCreate() {
        super.onCreate()
        renderer = MainTileRenderer(this)
        state = StepUtil.instance.observeSteps()
            .map { MainTileState(steps = it, index = 0) }
            .stateIn(lifecycleScope, SharingStarted.WhileSubscribed(5000), null)
    }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources {
        return renderer.produceRequestedResources(Unit, requestParams)
    }

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val latestState = state.filterNotNull().first()
        return renderer.renderTimeline(latestState, requestParams)
    }
}

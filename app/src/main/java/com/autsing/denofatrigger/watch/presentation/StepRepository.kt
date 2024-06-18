package com.autsing.denofatrigger.watch.presentation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.wear.tiles.TileService
import com.autsing.denofatrigger.watch.tile.MainTileService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class Step(
    val name: String,
    val url: String,
)

@Singleton
class StepRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @StepDataStore private val stepDataStore: DataStore<Preferences>,
) {
    companion object {
        lateinit var instance: StepRepository
    }

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private val steps: MutableStateFlow<List<Step>> = MutableStateFlow(emptyList())
    private val stepIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    init {
        loadState()
        listenSteps()
        listenStepIndex()
    }

    private fun loadState(): Job = coroutineScope.launch {
        steps.value = runCatching {
            val stepsString = stepDataStore.data
                .map { it[DataStoreModule.PrefKeys.prefKeySteps] ?: "" }
                .first()
            Json.decodeFromString<List<Step>>(stepsString)
        }.getOrDefault(emptyList())
        stepIndex.value = stepDataStore.data
            .map { it[DataStoreModule.PrefKeys.prefKeyStepIndex] ?: 0 }
            .first()
            .let { if (it < 0 || it > steps.value.size - 1) 0 else it }
    }

    private fun listenSteps(): Job = coroutineScope.launch {
        steps.collectIndexed { index, steps ->
            if (index == 0) {
                return@collectIndexed
            }

            stepDataStore.edit {
                val stepsString = Json.encodeToString(steps)
                it[DataStoreModule.PrefKeys.prefKeySteps] = stepsString
            }

            TileService.getUpdater(context).requestUpdate(MainTileService::class.java)
        }
    }

    private fun listenStepIndex(): Job = coroutineScope.launch {
        stepIndex.collectIndexed { index, stepIndex ->
            if (index == 0) {
                return@collectIndexed
            }

            stepDataStore.edit {
                it[DataStoreModule.PrefKeys.prefKeyStepIndex] = stepIndex
            }

            withContext(Dispatchers.Main) {
                TileService.getUpdater(context).requestUpdate(MainTileService::class.java)
            }
        }
    }

    fun getSteps(): List<Step> {
        return steps.value
    }

    fun getStepIndex(): Int {
        return stepIndex.value
    }

    fun observeSteps(): Flow<List<Step>> {
        return steps
    }

    fun observeStepIndex(): Flow<Int> {
        return stepIndex
    }

    fun addStep(index: Int, step: Step) {
        steps.value = steps.value
            .toMutableList()
            .apply {
                if (steps.value.isEmpty()) {
                    add(step)
                    stepIndex.value = 0
                } else {
                    add(index, step)
                    stepIndex.value = index
                }
            }
    }

    fun removeStep(index: Int) {
        steps.value = steps.value
            .toMutableList()
            .apply { removeAt(index) }
        if (steps.value.size - 1 > stepIndex.value) {
            stepIndex.value--
        }
    }

    fun setStepIndex(index: Int) {
        if (index < 0 || index > steps.value.size - 1) {
            stepIndex.value = 0
            return
        }
        stepIndex.value = index
    }
}

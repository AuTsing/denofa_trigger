package com.autsing.denofatrigger.watch.presentation

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.wear.tiles.TileService
import com.autsing.denofatrigger.watch.tile.MainTileService
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

@Serializable
data class Step(
    val name: String,
    val url: String,
)

class StepRepository(
    private val context: Context,
    private val stepDataStore: DataStore<Preferences>,
) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: StepRepository

        private const val PREF_KEY_STEPS = "pref_key_steps"
        private const val PREF_KEY_STEP_INDEX = "pref_key_step_index"

        private val prefKeySteps = stringPreferencesKey(PREF_KEY_STEPS)
        private val prefKeyStepIndex = intPreferencesKey(PREF_KEY_STEP_INDEX)
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
                .map { it[prefKeySteps] ?: "" }
                .first()
            Json.decodeFromString<List<Step>>(stepsString)
        }.getOrDefault(emptyList())
        stepIndex.value = stepDataStore.data
            .map { it[prefKeyStepIndex] ?: 0 }
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
                it[prefKeySteps] = stepsString
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
                it[prefKeyStepIndex] = stepIndex
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
                } else {
                    add(index, step)
                }
            }
        setStepIndex(index)
    }

    fun removeStep(index: Int) {
        steps.value = steps.value
            .toMutableList()
            .apply { removeAt(index) }
        setStepIndex(stepIndex.value - 1)
    }

    fun setStepIndex(index: Int) {
        if (index < 0 || index > steps.value.size - 1) {
            stepIndex.value = 0
            return
        }
        stepIndex.value = index
    }
}

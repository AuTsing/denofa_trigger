package com.autsing.denofatrigger.watch.presentation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @StepDataStore private val stepDataStore: DataStore<Preferences>,
    private val stepUtil: StepUtil,
) : ViewModel() {
    val steps: StateFlow<List<Step>> = stepUtil.observeSteps(this)

    init {
        loadSteps()
        listenSteps()
    }

    private fun loadSteps(): Job = viewModelScope.launch(Dispatchers.IO) {
        val steps = runCatching {
            val names = stepDataStore.data
                .map { it[DataStoreModule.PrefKeys.prefKeyStepNames] ?: "" }
                .first()
                .split(",")
            val urls = stepDataStore.data
                .map { it[DataStoreModule.PrefKeys.prefKeyStepUrls] ?: "" }
                .first()
                .split(",")
            names.mapIndexed { index, name -> Step(name, urls[index]) }
        }.getOrDefault(emptyList())
        stepUtil.setSteps(steps)
    }

    private fun listenSteps(): Job = viewModelScope.launch(Dispatchers.IO) {
        steps.collectIndexed { index, steps ->
            if (index == 0) {
                return@collectIndexed
            }

            val names = steps.joinToString(",") { it.name }
            val urls = steps.joinToString(",") { it.url }
            stepDataStore.edit {
                it[DataStoreModule.PrefKeys.prefKeyStepNames] = names
                it[DataStoreModule.PrefKeys.prefKeyStepUrls] = urls
            }
        }
    }

    fun handleRemoveStep(index: Int) {
        stepUtil.removeStep(index)
    }

    fun handleAddStep(context: Context, index: Int) {
        AddStepActivity.startActivity(context, index)
    }
}

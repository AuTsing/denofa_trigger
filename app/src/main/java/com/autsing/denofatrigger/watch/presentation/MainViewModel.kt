package com.autsing.denofatrigger.watch.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stepRepo: StepRepository,
) : ViewModel() {
    val steps: StateFlow<List<Step>> = stepRepo.observeSteps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val stepIndex: StateFlow<Int> = stepRepo.observeStepIndex()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun handleAddStep(context: Context) {
        AddStepActivity.startActivity(context, stepIndex.value)
    }

    fun handleRemoveStep() {
        stepRepo.removeStep(stepIndex.value)
    }

    fun handlePrevStep() {
        stepRepo.setStepIndex(stepIndex.value - 1)
    }

    fun handleNextStep() {
        stepRepo.setStepIndex(stepIndex.value + 1)
    }
}

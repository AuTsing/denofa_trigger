package com.autsing.denofatrigger.watch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepUtil @Inject constructor(
    private val stepRepo: StepRepository,
) {
    companion object {
        lateinit var instance: StepUtil
    }

    fun getSteps(): List<Step> {
        return stepRepo.getSteps()
    }

    fun observeSteps(): Flow<List<Step>> {
        return stepRepo.observeSteps()
    }

    fun observeSteps(viewModel: ViewModel): StateFlow<List<Step>> {
        return stepRepo.observeSteps()
            .stateIn(viewModel.viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun setSteps(steps: List<Step>) {
        stepRepo.setSteps(steps)
    }

    fun removeStep(index: Int) {
        stepRepo.removeStep(index)
    }
}

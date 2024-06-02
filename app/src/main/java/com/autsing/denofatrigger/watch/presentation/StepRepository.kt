package com.autsing.denofatrigger.watch.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class Step(
    val name: String,
    val url: String,
)

@Singleton
class StepRepository @Inject constructor() {
    private val steps: MutableStateFlow<List<Step>> = MutableStateFlow(emptyList())

    fun getSteps(): List<Step> {
        return steps.value
    }

    fun observeSteps(): Flow<List<Step>> {
        return steps
    }

    fun setSteps(newSteps: List<Step>) {
        steps.value = newSteps
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
    }

    fun removeStep(index: Int) {
        steps.value = steps.value
            .toMutableList()
            .apply { removeAt(index) }
    }
}

package com.autsing.denofatrigger.watch.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class Step(
    val name: String,
    val url: String,
)

class StepRepository {
    private val steps: MutableStateFlow<List<Step>> = MutableStateFlow(
        listOf(
            Step("Step1", ""),
            Step("Step2", ""),
            Step("Step3", ""),
            Step("Step4", ""),
        )
    )

    fun getSteps(): List<Step> {
        return steps.value
    }

    fun observeSteps(): Flow<List<Step>> {
        return steps
    }

    fun addStep(index: Int, step: Step) {
        steps.value = steps.value
            .toMutableList()
            .apply { add(index, step) }
    }

    fun removeStep(index: Int) {
        steps.value = steps.value
            .toMutableList()
            .apply { removeAt(index) }
    }
}

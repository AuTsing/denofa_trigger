/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.autsing.denofatrigger.watch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.Text
import com.autsing.denofatrigger.watch.R
import com.autsing.denofatrigger.watch.presentation.theme.DenofaTriggerTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainActivity : ComponentActivity() {
    private val stepsState: StateFlow<List<Step>> = StepRepository.instance.observeSteps()
        .stateIn(lifecycleScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val stepIndexState: StateFlow<Int> = StepRepository.instance.observeStepIndex()
        .stateIn(lifecycleScope, SharingStarted.WhileSubscribed(5000), 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            val steps by stepsState.collectAsState()
            val stepIndex by stepIndexState.collectAsState()

            WearScreen(
                steps = steps,
                stepIndex = stepIndex,
                onAddStep = { AddStepActivity.startActivity(this, stepIndex) },
                onRemoveStep = { StepRepository.instance.removeStep(stepIndex) },
                onPrevStep = { StepRepository.instance.setStepIndex(stepIndex - 1) },
                onNextStep = { StepRepository.instance.setStepIndex(stepIndex + 1) },
            )
        }
    }
}

@Composable
private fun WearScreen(
    steps: List<Step>,
    stepIndex: Int,
    onAddStep: () -> Unit = {},
    onRemoveStep: () -> Unit = {},
    onPrevStep: () -> Unit = {},
    onNextStep: () -> Unit = {},
) {
    DenofaTriggerTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(6.dp),
        ) {
            if (steps.isNotEmpty()) {
                Text("${stepIndex + 1}/${steps.size}")
            } else {
                Text(
                    text = stringResource(R.string.text_no_steps),
                    style = MaterialTheme.typography.title2,
                    modifier = Modifier.padding(6.dp),
                )
            }

            if (steps.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (stepIndex > 0) {
                        OutlinedButton(
                            border = ButtonDefaults.buttonBorder(),
                            onClick = onPrevStep,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_arrow_back_ios_24),
                                contentDescription = "left button",
                            )
                        }
                    } else {
                        OutlinedButton(
                            border = ButtonDefaults.buttonBorder(),
                            enabled = false,
                            onClick = { },
                        ) { }
                    }

                    Text(
                        text = steps[stepIndex].name,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.title2,
                        modifier = Modifier.weight(1F),
                    )

                    if (stepIndex < steps.size - 1) {
                        OutlinedButton(
                            border = ButtonDefaults.buttonBorder(),
                            onClick = onNextStep,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_arrow_forward_ios_24),
                                contentDescription = "right button",
                            )
                        }
                    } else {
                        OutlinedButton(
                            border = ButtonDefaults.buttonBorder(),
                            enabled = false,
                            onClick = { },
                        ) { }
                    }
                }
            }

            Row {
                if (steps.isNotEmpty()) {
                    Button(
                        onClick = onRemoveStep,
                        colors = ButtonDefaults.secondaryButtonColors(),
                        modifier = Modifier.padding(6.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.round_delete_24),
                            contentDescription = "delete button",
                        )
                    }
                }
                Button(
                    onClick = { onAddStep() },
                    modifier = Modifier.padding(6.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_add_24),
                        contentDescription = "add button",
                    )
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearScreen(
        steps = emptyList(),
        stepIndex = 0,
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreviewNotEmpty() {
    WearScreen(
        steps = listOf(
            Step("Step1", ""),
            Step("Step2", ""),
            Step("Step3", ""),
        ),
        stepIndex = 0,
    )
}

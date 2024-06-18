package com.autsing.denofatrigger.watch.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import com.autsing.denofatrigger.watch.R
import com.autsing.denofatrigger.watch.presentation.theme.DenofaTriggerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddStepActivity : ComponentActivity() {
    companion object {
        const val EXTRA_KEY_INDEX = "EXTRA_KEY_INDEX"

        fun startActivity(context: Context, index: Int) {
            val intent = Intent(context, AddStepActivity::class.java)
            intent.putExtra(EXTRA_KEY_INDEX, index)
            context.startActivity(intent)
        }
    }

    @Inject
    lateinit var stepRepo: StepRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        val index = intent.getIntExtra(EXTRA_KEY_INDEX, -1)

        setContent {
            AddStepApp(
                index = index,
                onConfirm = { index, name, url ->
                    stepRepo.addStep(index, Step(name, url))
                    finish()
                },
                onCancel = { finish() },
            )
        }
    }
}

@Composable
private fun AddStepApp(
    index: Int,
    onConfirm: (Int, String, String) -> Unit = { _, _, _ -> },
    onCancel: () -> Unit = {},
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var addBeforeIndex by remember { mutableStateOf(false) }

    DenofaTriggerTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(6.dp),
        ) {
            ScalingLazyColumn {
                item {
                    Text(stringResource(R.string.label_name))
                }
                item {
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        textStyle = TextStyle(
                            color = MaterialTheme.colors.primaryVariant,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { softwareKeyboardController?.hide() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colors.onPrimary,
                                shape = MaterialTheme.shapes.medium,
                            )
                            .padding(6.dp),
                    )
                }
                item {
                    Text(stringResource(R.string.label_url))
                }
                item {
                    BasicTextField(
                        value = url,
                        onValueChange = { url = it },
                        textStyle = TextStyle(
                            color = MaterialTheme.colors.primaryVariant,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { softwareKeyboardController?.hide() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colors.onPrimary,
                                shape = MaterialTheme.shapes.medium,
                            )
                            .padding(6.dp),
                    )
                }
                item {
                    ToggleChip(
                        checked = addBeforeIndex,
                        onCheckedChange = { addBeforeIndex = !addBeforeIndex },
                        label = {
                            Text(stringResource(R.string.text_add_before_index, index + 1))
                        },
                        toggleControl = {
                            Switch(
                                checked = addBeforeIndex,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
                item {
                    Row {
                        Button(
                            onClick = { onCancel() },
                            colors = ButtonDefaults.secondaryButtonColors(),
                            modifier = Modifier.padding(6.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_clear_24),
                                contentDescription = "cancel button",
                            )
                        }
                        Button(
                            onClick = {
                                val idx = if (addBeforeIndex) {
                                    index
                                } else {
                                    index + 1
                                }
                                onConfirm(idx, name, url)
                            },
                            modifier = Modifier.padding(6.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_check_24),
                                contentDescription = "confirm button",
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewAddStepApp() {
    AddStepApp(index = 0)
}

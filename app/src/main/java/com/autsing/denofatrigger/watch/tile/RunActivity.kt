package com.autsing.denofatrigger.watch.tile

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.autsing.denofatrigger.watch.R
import com.autsing.denofatrigger.watch.presentation.StepRepository
import com.autsing.denofatrigger.watch.presentation.theme.DenofaTriggerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class RunActivity : ComponentActivity() {
    private var textState: MutableStateFlow<String> = MutableStateFlow("")
    private var infoState: MutableStateFlow<String?> = MutableStateFlow(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            val text = textState.collectAsState()
            val info = infoState.collectAsState()
            RunActivityAppScreen(
                text = text.value,
                info = info.value,
            )
        }

        lifecycleScope.launch(Dispatchers.IO) {
            var maybeResponse: Response? = null

            runCatching {
                textState.value = "${getString(R.string.text_sending_request)}..."

                val steps = StepRepository.instance.getSteps()
                val stepIndex = StepRepository.instance.getStepIndex()
                val step = runCatching { steps[stepIndex] }.getOrNull()
                    ?: throw Exception("Unreachable index")

                val client = OkHttpClient()
                val request = Request.Builder().url(step.url).build()
                val response = client.newCall(request).execute().also { maybeResponse = it }
                val content = response.body?.string() ?: response.message
                if (!response.isSuccessful) {
                    throw Exception(content)
                }

                textState.value = getString(R.string.text_has_been_sent)
                infoState.value = content
                StepRepository.instance.setStepIndex(stepIndex + 1)
                delay(3000)
                finishAndRemoveTask()
            }.onFailure {
                textState.value = getString(R.string.text_failed)
                infoState.value = it.message ?: it.stackTraceToString()
            }.also {
                runCatching { maybeResponse?.close() }
            }
        }
    }
}

@Composable
private fun RunActivityAppScreen(
    text: String,
    info: String? = null,
) {
    val context = LocalContext.current

    DenofaTriggerTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(6.dp),
        ) {
            if (info == null) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.title2,
                )
            } else {
                ScalingLazyColumn {
                    item {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.title2,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                        )
                    }
                    item {
                        Text(
                            text = info,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                        )
                    }
                    item {
                        Button(
                            onClick = { (context as Activity).finishAndRemoveTask() },
                            modifier = Modifier.padding(6.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_clear_24),
                                contentDescription = "exit button",
                            )
                        }
                    }
                }
            }
        }
    }
}

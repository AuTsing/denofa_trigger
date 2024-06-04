package com.autsing.denofatrigger.watch.tile

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.CircularProgressIndicator
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.EdgeContentLayout
import com.autsing.denofatrigger.watch.R
import com.autsing.denofatrigger.watch.presentation.Step
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer

@OptIn(ExperimentalHorologistApi::class)
class MainTileRenderer(context: Context) : SingleTileLayoutRenderer<MainTileState, Unit>(context) {
    companion object {
        const val RES_PLAY_ARROW = "res_play_arrow"
    }

    override fun renderTile(
        state: MainTileState,
        deviceParameters: DeviceParameters,
    ): LayoutElement {
        return tileLayout(
            context = context,
            deviceParameters = deviceParameters,
            state = state,
            runButtonClickable = ModifiersBuilders.Clickable.Builder().build(),
        )
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceState: Unit,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>,
    ) {
        addIdToImageMapping(
            RES_PLAY_ARROW,
            drawableResToImageResource(R.drawable.round_play_arrow_24),
        )
    }
}

private fun tileLayout(
    context: Context,
    deviceParameters: DeviceParameters,
    state: MainTileState,
    runButtonClickable: ModifiersBuilders.Clickable,
): LayoutElement {
    return EdgeContentLayout.Builder(deviceParameters)
        .apply {
            val text = runCatching { state.steps[state.index] }.getOrNull()

            if (text != null) {
                setContent(
                    LayoutElementBuilders.Column.Builder()
                        .addContent(infoLayout(context, text.name))
                        .addContent(runLayout(context, runButtonClickable))
                        .build()
                )
            } else {
                setContent(
                    LayoutElementBuilders.Column.Builder()
                        .addContent(infoLayout(context, context.getString(R.string.text_no_steps)))
                        .build()
                )
            }
        }
        .apply {
            val percentage = runCatching { state.index.toFloat() / state.steps.size }
                .getOrNull()

            if (percentage != null) {
                setEdgeContent(progressLayout(percentage))
            } else {
                setEdgeContent(progressLayout(0F))
            }
        }
        .build()
}

private fun infoLayout(
    context: Context,
    text: String,
): Text {
    return Text.Builder(context, text)
        .setColor(ColorBuilders.argb(Colors.DEFAULT.onSurface))
        .setTypography(Typography.TYPOGRAPHY_TITLE1)
        .setModifiers(
            ModifiersBuilders.Modifiers.Builder()
                .setPadding(
                    ModifiersBuilders.Padding.Builder()
                        .setAll(DimensionBuilders.dp(16F))
                        .build()
                )
                .build()
        )
        .build()
}

private fun runLayout(
    context: Context,
    clickable: ModifiersBuilders.Clickable,
): Button {
    return Button.Builder(context, clickable)
        .setIconContent(MainTileRenderer.RES_PLAY_ARROW)
        .build()
}

private fun progressLayout(
    percentage: Float,
): CircularProgressIndicator {
    return CircularProgressIndicator.Builder()
        .setProgress(percentage)
        .build()
}

@OptIn(ExperimentalHorologistApi::class)
@WearPreviewLargeRound
@Composable
fun TilePreview() {
    TileLayoutPreview(
        state = MainTileState(
            steps = listOf(
                Step("Step1", ""),
                Step("Step2", ""),
                Step("Step3", ""),
                Step("Step4", ""),
                Step("Step5", ""),
            ),
            index = 0,
        ),
        resourceState = Unit,
        renderer = MainTileRenderer(LocalContext.current),
    )
}

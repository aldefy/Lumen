package com.aditlal.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aditlal.sample.examples.AnimationsExample
import com.aditlal.sample.examples.BasicExample
import com.aditlal.sample.examples.ConnectorsExample
import com.aditlal.sample.examples.DialogCoordinationExample
import com.aditlal.sample.examples.ExampleGallery
import com.aditlal.sample.examples.LazyColumnExample
import com.aditlal.sample.examples.ScrimOpacityExample
import com.aditlal.sample.examples.SequenceExample
import com.aditlal.sample.examples.ShapesExample
import com.aditlal.sample.examples.ThemingExample
import com.aditlal.sample.examples.TooltipOptionsExample
import com.aditlal.sample.examples.TapThroughExample
import com.aditlal.sample.examples.TooltipPositionExample
import com.aditlal.sample.ui.theme.LumenSampleTheme

@Composable
fun App() {
    LumenSampleTheme {
        var currentExample by remember { mutableStateOf<Example?>(null) }

        PlatformBackHandler(enabled = currentExample != null) {
            currentExample = null
        }

        when (currentExample) {
            null -> ExampleGallery(
                modifier = Modifier.fillMaxSize(),
                onExampleSelected = { currentExample = it }
            )
            Example.BASIC -> BasicExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.SEQUENCE -> SequenceExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.SHAPES -> ShapesExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.CONNECTORS -> ConnectorsExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.ANIMATIONS -> AnimationsExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.TOOLTIP_POSITION -> TooltipPositionExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.TOOLTIP_OPTIONS -> TooltipOptionsExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.THEMING -> ThemingExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.SCRIM_OPACITY -> ScrimOpacityExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.LAZY_COLUMN -> LazyColumnExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.DIALOG_COORDINATION -> DialogCoordinationExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
            Example.TAP_THROUGH -> TapThroughExample(
                modifier = Modifier.fillMaxSize(),
                onBack = { currentExample = null }
            )
        }
    }
}

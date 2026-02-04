package com.aditlal.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.aditlal.sample.examples.TooltipPositionExample
import com.aditlal.sample.ui.theme.LumenSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LumenSampleTheme {
                var currentExample by remember { mutableStateOf<Example?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    when (currentExample) {
                        null -> ExampleGallery(
                            modifier = Modifier.padding(paddingValues),
                            onExampleSelected = { currentExample = it }
                        )
                        Example.BASIC -> BasicExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.SEQUENCE -> SequenceExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.SHAPES -> ShapesExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.CONNECTORS -> ConnectorsExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.ANIMATIONS -> AnimationsExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.TOOLTIP_POSITION -> TooltipPositionExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.TOOLTIP_OPTIONS -> TooltipOptionsExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.THEMING -> ThemingExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.SCRIM_OPACITY -> ScrimOpacityExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.LAZY_COLUMN -> LazyColumnExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                        Example.DIALOG_COORDINATION -> DialogCoordinationExample(
                            modifier = Modifier.padding(paddingValues),
                            onBack = { currentExample = null }
                        )
                    }
                }
            }
        }
    }
}

enum class Example(val title: String, val description: String) {
    BASIC("Basic Coachmark", "Single target with pulse animation"),
    SEQUENCE("Multi-Step Sequence", "5-step tour with progress indicator"),
    SHAPES("Shapes Showcase", "Circle, Rect, RoundedRect, Squircle, Star"),
    CONNECTORS("Connector Styles", "Vertical, Horizontal, Elbow, Direct, Auto"),
    ANIMATIONS("Highlight Animations", "None, Pulse, Glow, Ripple, Shimmer, Bounce"),
    TOOLTIP_POSITION("Tooltip Position", "Top, Bottom, Start, End, Auto"),
    TOOLTIP_OPTIONS("Tooltip Options", "Card wrapper, Skip button, CTA text"),
    THEMING("Theming & Colors", "Custom color schemes"),
    SCRIM_OPACITY("Scrim Opacity", "Light, Medium, Dark, Extra Dark"),
    LAZY_COLUMN("LazyColumn", "Coachmarks in scrollable lists"),
    DIALOG_COORDINATION("Dialog Coordination", "Auto-dismiss when dialogs appear"),
}

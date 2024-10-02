import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import di.DesktopApplicationComponent
import di.WindowComponent
import di.create

fun main() = application {
    val applicationComponent = remember {
        DesktopApplicationComponent::class.create()
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Playground",
        state = rememberWindowState(width = 250.dp, height = 400.dp),
    ) {
        val component = remember(applicationComponent) {
            WindowComponent::class.create(applicationComponent)
        }

        component.appContent.Content(
            modifier = Modifier,
        )
    }
}

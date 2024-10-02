package me.oikvpqya.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import di.MainActivityComponent
import di.applicationComponent
import di.create

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = MainActivityComponent::class.create(applicationComponent)

        setContent {
            component.appContent.Content(
                modifier = Modifier,
            )
        }
    }
}

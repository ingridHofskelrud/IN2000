package no.uio.ifi.in2000.team33.smaafly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team33.smaafly.ui.navigation.Controller
import no.uio.ifi.in2000.team33.smaafly.ui.theme.SmaaflyAppTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmaaflyAppTheme {

                Controller()
            }
        }
    }
}



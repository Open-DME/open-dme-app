import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManager.Listener
import com.mmk.kmpnotifier.notification.PayloadData
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import saschpe.log4k.Log


@Composable
@Preview
fun App() {
    var persistedOpenIdConfig by mutableSetting()
    var openIdConfig: String by remember { mutableStateOf(persistedOpenIdConfig) }

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    NotifierManager.addListener(listener = object: Listener {
        override fun onNewToken(token: String) {
            Log.warn {
                "onNewToken: $token"
            }
        }

        override fun onNotificationClicked(data: PayloadData) {
            super.onNotificationClicked(data)
            Log.info {
                "Notification got clicked with data: $data"
            }
        }

        override fun onPayloadData(data: PayloadData) {
            super.onPayloadData(data)
            Log.info {
                "OnPayloadData with data: $data"
            }
        }

        override fun onPushNotification(title: String?, body: String?) {
            super.onPushNotification(title, body)
            Log.info {
                "onPushNotification with title: $title and body: $body"
            }
        }
    })

    MaterialTheme {
        Scaffold(snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }) {
            if (openIdConfig.isEmpty()) {
                QrScannerCompose(openIdConfig) {
                    openIdConfig = it
                    persistedOpenIdConfig = it
                }
            } else {
                HomeElement(HomeData(openIdConfig)) {
                    openIdConfig = ""
                    persistedOpenIdConfig = ""
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Qr Code invalid")
                    }
                }
            }
        }
    }
}
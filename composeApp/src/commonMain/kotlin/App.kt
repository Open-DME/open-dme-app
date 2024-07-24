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
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    var persistedOpenIdConfig by mutableSetting()
    var openIdConfig: String by remember { mutableStateOf(persistedOpenIdConfig) }

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
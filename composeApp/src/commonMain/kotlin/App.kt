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
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    var persistedOpenIdConfig by mutableSetting()
    var openIdConfig: String by remember { mutableStateOf(persistedOpenIdConfig) }
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val json = Json
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

                val openId: OpenId
                try {
                    openId = json.decodeFromString<OpenId>(openIdConfig)
                } catch (ex: SerializationException) {
                    openIdConfig = ""
                    persistedOpenIdConfig = ""
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Qr Code invalid")
                    }
                    return@Scaffold
                }

                var client: HttpClient?

                var homeData: HomeData? by remember { mutableStateOf(null) }

                HomeElement(homeData) {
                    openIdConfig = ""
                }

                coroutineScope.launch {
                    createOpenIdClient(openId).also { openIdClient ->
                        client = createClient(openIdClient).also { httpClient ->
                            homeData = HomeData(
                                openIdClient,
                                openIdConfig,
                                httpClient,
                                openId
                            )
                        }

                    }
                }
            }
        }
    }
}
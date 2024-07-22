import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse

@Composable
@Preview
fun App() {
    var persistedOpenIdConfig by mutableSetting()
    var persistedAccessToken by mutableSetting()


    var openIdConfig: String by remember { mutableStateOf(persistedOpenIdConfig) }
    var loggedIn by remember { mutableStateOf(false) }
    var accessToken: AccessTokenResponse? by remember { mutableStateOf(null) }

    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }



    val json = Json
    MaterialTheme {
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
                return@MaterialTheme
            }

            val openIdConnectClient =
                createOpenIdClient(openId)
            coroutineScope.launch {
                openIdConnectClient.discover()
            }
            val client = createClient(openIdConnectClient)

            val flow = getAuthFlowFactory().createAuthFlow(openIdConnectClient)

            if (!loggedIn) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Login",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                    )
                    Text(
                        text = openIdConfig,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                    )


                    Button(
                        onClick = {
                            coroutineScope.launch {
                                accessToken = flow.getAccessToken()
                                persistedAccessToken = json.encodeToString(accessToken)
                            }

                            loggedIn = loggedIn.not()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Log in")
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = { openIdConfig = "" },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Reset URL")
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    //Text(text = "Token: $persistedAccessToken")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                accessToken?.id_token?.let { openIdConnectClient.endSession(it) }
                                loggedIn = loggedIn.not()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Log out")
                    }
                }
            }

        }
    }
}
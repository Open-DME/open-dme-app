import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.OpenIdConnectException
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse

@Composable
fun OpenIdLoginButton(
    openIdConnectClient: OpenIdConnectClient,
    label: String = "Log in",
    onFailure: (message: String) -> Unit = {},
    onSuccess: (token: AccessTokenResponse) -> Unit
) {
    var persistedAccessToken by mutableSetting()

    val coroutineScope = rememberCoroutineScope()
    Button(
        onClick = {
            coroutineScope.launch {
                try {
                    val flow = getAuthFlowFactory().createAuthFlow(openIdConnectClient)
                    val accessToken = flow.getAccessToken()
                    persistedAccessToken = Json.encodeToString(accessToken)
                    onSuccess.invoke(accessToken)
                } catch (ex: OpenIdConnectException) {
                    onFailure.invoke(ex.message)
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label)
    }
}

@Composable
fun OpenIdLogoutButton(
    openIdConnectClient: OpenIdConnectClient,
    accessTokenResponse: AccessTokenResponse?,
    label: String = "Log out",
    onLogout: () -> Unit
) {
    var persistedAccessToken by mutableSetting()
    val coroutineScope = rememberCoroutineScope()
    Button(
        onClick = {
            coroutineScope.launch {
                accessTokenResponse?.id_token?.let {
                    openIdConnectClient.endSession(
                        it
                    )
                }
                onLogout.invoke()
                persistedAccessToken = ""
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label)
    }
}
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
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
import com.mmk.kmpnotifier.notification.NotifierManager
import dto.ApiService
import dto.Responses
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.tokenstore.saveTokens
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse
import saschpe.log4k.Log

data class HomeData(
    var openIdConfig: String,
)

@OptIn(ExperimentalOpenIdConnect::class)
@Composable
fun HomeElement(homeData: HomeData, onResetConfig: () -> Unit) {
    var accessToken: AccessTokenResponse? by remember { mutableStateOf(null) }
    var openIdConnectClient: OpenIdConnectClient? by remember { mutableStateOf(null) }


    val openId: OpenId
    try {
        openId = Json.decodeFromString<OpenId>(homeData.openIdConfig)
    } catch (ex: SerializationException) {
        Log.error(ex) { "Invalid Qr Code" }
        onResetConfig.invoke()
        return
    }
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch {
        openIdConnectClient = createOpenIdClient(openId)
    }

    openId.topic.let {
        coroutineScope.launch {
            NotifierManager.getPushNotifier().subscribeToTopic(it)
        }
    }

    val tokenStore = getStore()

    if (accessToken == null) {
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

            openIdConnectClient?.let {
                OpenIdLoginButton(openIdConnectClient = it) {
                    accessToken = it
                    CoroutineScope(Dispatchers.Default).launch {
                        tokenStore.saveTokens(it)
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    homeData.openIdConfig = ""
                    onResetConfig.invoke()
                },
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

            var parsedResponse: Responses.Test? by remember { mutableStateOf(null) }
            var errorMessage: String? by remember { mutableStateOf(null) }

            val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
                Log.error(throwable) { "Network call failed" }
            }

            openIdConnectClient?.let {
                val httpClient = createClient(it, openId, tokenStore)
                val apiService = ApiService(httpClient)

                CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {

                    apiService.test(onSuccess = { body ->
                        parsedResponse = body
                    }, onError = { status, body ->
                        errorMessage = "$status and $body"
                    })
                }
            }

            Text(
                text = errorMessage ?: ""
            )

            Text(
                text = parsedResponse?.name ?: ""
            )

            Text(
                text = "Roles",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
            )

            parsedResponse?.roles?.forEach {
                Text(
                    text = it
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            openIdConnectClient?.let {
                OpenIdLogoutButton(it, accessToken) {
                    accessToken = null
                }
            }

        }
    }

}


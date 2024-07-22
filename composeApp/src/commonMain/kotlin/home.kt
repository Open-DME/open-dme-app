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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse

data class HomeData(
    var openIdConnectClient: OpenIdConnectClient,
    var openIdConfig: String
)

@Composable
fun HomeElement(homeData: HomeData?, onResetConfig: () -> Unit) {
    var accessToken: AccessTokenResponse? by remember { mutableStateOf(null) }
    homeData ?: return

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

            OpenIdLoginButton(openIdConnectClient = homeData.openIdConnectClient) {
                accessToken = it
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
            Spacer(modifier = Modifier.height(16.dp))
            OpenIdLogoutButton(homeData.openIdConnectClient, accessToken) {
                accessToken = null
            }
        }
    }

}
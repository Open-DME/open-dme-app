import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import kotlinx.serialization.Serializable
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.ktor.oidcBearer
import org.publicvalue.multiplatform.oidc.tokenstore.TokenRefreshHandler
import org.publicvalue.multiplatform.oidc.types.CodeChallengeMethod

@Serializable
data class OpenId(
    var discoverUrl: String,
    var clientId: String,
    var clientSecret: String,
    var scope: String
)


suspend fun createOpenIdClient(networkConfig: OpenId): OpenIdConnectClient {
    val client = OpenIdConnectClient(discoveryUri = networkConfig.discoverUrl) {
        clientId = networkConfig.clientId
        clientSecret = networkConfig.clientSecret
        scope = networkConfig.scope
        codeChallengeMethod = CodeChallengeMethod.S256
        redirectUri = PlatformConstants.redirectUrl
    }
    client.discover()
    return client
}

@OptIn(ExperimentalOpenIdConnect::class)
fun createClient(client: OpenIdConnectClient): HttpClient {
    val tokenStore = getStore()
    val refreshHandler = TokenRefreshHandler(tokenStore)
    return HttpClient {
        install(Auth) {
            oidcBearer(
                tokenStore = tokenStore,
                refreshHandler = refreshHandler,
                client = client,
            )
        }
    }
}
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
    var scope: String,
    var hostName: String
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
        install(HttpCookies)
        install(Logging) {
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000L
            connectTimeoutMillis = 15000L
            socketTimeoutMillis = 15000L
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    explicitNulls = false
                }
            )
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
        install(Auth) {
            oidcBearer(
                tokenStore = tokenStore,
                refreshHandler = refreshHandler,
                client = client,
            )
        }
    }
}
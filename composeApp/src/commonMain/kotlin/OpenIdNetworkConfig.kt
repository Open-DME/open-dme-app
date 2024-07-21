import org.publicvalue.multiplatform.oidc.types.CodeChallengeMethod

data class OpenIdNetworkConfig(
    var discoverUrl: String,
    var clientId: String = "<clientId>",
    var clientSecret: String = "<clientSecret>",
    var scope: String = "openid profile",

    var redirectUri: String = "<redirectUri>"
) {
    object CONS {
        val codeChallengeMethod: CodeChallengeMethod = CodeChallengeMethod.S256
    }
}

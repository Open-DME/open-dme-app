import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.tokenstore.TokenStore


@OptIn(ExperimentalOpenIdConnect::class)
expect fun getStore(): TokenStore


expect fun getAuthFlowFactory(): CodeAuthFlowFactory
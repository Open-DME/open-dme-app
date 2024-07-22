import io.github.opendme.MainActivity
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.tokenstore.AndroidSettingsTokenStore
import org.publicvalue.multiplatform.oidc.tokenstore.TokenStore

@OptIn(ExperimentalOpenIdConnect::class)
actual fun getStore(): TokenStore = AndroidSettingsTokenStore(MainActivity.instance.applicationContext)

actual fun getAuthFlowFactory() : CodeAuthFlowFactory = MainActivity.codeAuthFlowFactory
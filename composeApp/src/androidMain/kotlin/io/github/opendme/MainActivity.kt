package io.github.opendme

import App
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.permission.permissionUtil
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory

class MainActivity : ComponentActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        val codeAuthFlowFactory = AndroidCodeAuthFlowFactory(useWebView = false)
        lateinit var instance: MainActivity
            private set

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        codeAuthFlowFactory.registerActivity(this)
        NotifierManager.onCreateOrOnNewIntent(null)

        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        NotifierManager.onCreateOrOnNewIntent(intent)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
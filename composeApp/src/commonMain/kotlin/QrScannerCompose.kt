import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import qrscanner.QrScanner
import saschpe.log4k.Log


/**
 * https://medium.com/mobile-innovation-network/qrkit-barcode-scanning-in-compose-multiplatform-for-android-and-ios-77cf5d84f719
 */
@Composable
fun QrScannerCompose(initialUrl: String, update: (String) -> Unit) {
    var startBarCodeScan by remember { mutableStateOf(false) }
    var qrCodeURL by remember { mutableStateOf(initialUrl) }

    var openImagePicker by remember { mutableStateOf(value = false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (qrCodeURL.isEmpty() && startBarCodeScan) {
                Column(
                    modifier = Modifier
                        .background(color = Color.Black)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = if (getPlatform().name != "Desktop") {
                            Modifier
                                .size(250.dp)
                                .clip(shape = RoundedCornerShape(size = 14.dp))
                                .clipToBounds()
                                .border(2.dp, Color.Gray, RoundedCornerShape(size = 14.dp))
                        } else {
                            Modifier
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        QrScanner(
                            modifier = Modifier
                                .clipToBounds()
                                .clip(shape = RoundedCornerShape(size = 14.dp)),
                            flashlightOn = false,
                            openImagePicker = false,
                            onCompletion = {
                                qrCodeURL = it
                                update.invoke(it)
                                startBarCodeScan = false
                            },
                            imagePickerHandler = {
                                openImagePicker = it
                            },
                            onFailure = {
                                coroutineScope.launch {
                                    if (it.isEmpty()) {
                                        snackbarHostState.showSnackbar("Invalid qr code")
                                    } else {
                                        snackbarHostState.showSnackbar(it)
                                    }
                                }
                            }
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            startBarCodeScan = true
                        }
                    ) {
                        Text(
                            text = "Scan Qr",
                            modifier = Modifier.background(Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
        if (startBarCodeScan) {
            Icon(
                imageVector = Icons.Filled.Close,
                "Close",
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp)
                    .size(24.dp)
                    .clickable {
                        startBarCodeScan = false
                    }.align(Alignment.TopEnd),
                tint = Color.White
            )
        }
    }
}
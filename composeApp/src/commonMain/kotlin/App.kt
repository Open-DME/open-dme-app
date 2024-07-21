import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import qrscanner.QrScanner

@Composable
@Preview
fun App() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loggedIn by remember { mutableStateOf(false) }
    var authUrl by remember { mutableStateOf("") }

    var snackBarHostState by mutableStateOf("")
    val coroutineScope = rememberCoroutineScope()


    MaterialTheme {

        if (authUrl.isBlank()) {
            QrScanner(
                modifier = Modifier
                    .clipToBounds()
                    .clip(shape = RoundedCornerShape(size = 14.dp)),
                onCompletion = {
                    authUrl = it
                },
                flashlightOn = false,
                openImagePicker = false,
                imagePickerHandler = {},
                onFailure = {
                    coroutineScope.launch {
                        if (it.isEmpty()) {
                            snackBarHostState = "Invalid qr code"
                        } else {
                            snackBarHostState = it
                        }
                    }
                }
            )
        } else {


            if (!loggedIn) {
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

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { loggedIn = loggedIn.not() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Log in")
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Email: $email")
                    Text(text = "Passwort: $password")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { loggedIn = loggedIn.not() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Log out")
                    }
                }
            }

        }
    }
}
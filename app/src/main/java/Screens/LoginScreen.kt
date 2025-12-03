package Screens

import Model.Rol
import ViewModel.LoginViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mitienda.theme.BlueDarkBackground
import com.example.mitienda.theme.BlueLightBackground
import com.example.mitienda.theme.BluePrimary
import com.example.mitienda.theme.ErrorRed
import com.example.mitienda.theme.InputBorder
import com.example.mitienda.theme.TextGray

@Composable
fun LoginScreen(
    onLoginSuccess: (Rol) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Fondo degradado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BlueDarkBackground, BlueLightBackground)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- HEADER (Texto de Bienvenida) ---
            Column(
                modifier = Modifier
                    .weight(0.35f) // Ocupa el 35% de arriba
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Bienvenido!",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Inicia sesión para continuar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // --- FORMULARIO (Tarjeta Blanca) ---
            Surface(
                modifier = Modifier.weight(0.65f), // Ocupa el 65% de abajo
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    ModernTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Ingrese su Correo",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ModernTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Ingrese su Contraseña",
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = BluePrimary)
                    } else {
                        ModernButton(
                            text = "Iniciar Sesión",
                            onClick = { viewModel.login(email.trim(), password.trim(), onLoginSuccess) }
                        )
                    }

                    errorMessage?.let { msg ->
                        Spacer(Modifier.height(16.dp))
                        Text(text = msg, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Footer
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("¿No tienes cuenta? ", color = TextGray)
                        TextButton(onClick = onNavigateToRegister) {
                            Text("Regístrate", color = BluePrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// --- Componentes Reutilizables (Cópialos al final del archivo o en uno aparte) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTextField(
    value: String, onValueChange: (String) -> Unit, label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BluePrimary,
            unfocusedBorderColor = InputBorder,
            focusedLabelColor = BluePrimary,
            unfocusedLabelColor = TextGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
}

@Composable
fun ModernButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
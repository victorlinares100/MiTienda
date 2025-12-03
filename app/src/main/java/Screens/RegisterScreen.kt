package Screens

import ViewModel.RegisterViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mitienda.theme.BlueDarkBackground
import com.example.mitienda.theme.BlueLightBackground
import com.example.mitienda.theme.BluePrimary
import com.example.mitienda.theme.ErrorRed
import com.example.mitienda.theme.TextGray
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(1500)
            onRegisterSuccess()
        }
    }

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

            // --- HEADER ---
            Column(
                modifier = Modifier
                    .weight(0.2f) // Un poco más pequeño que en login
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Crear\nCuenta",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // --- FORMULARIO ---
            Surface(
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    ModernTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre Completo")
                    Spacer(Modifier.height(12.dp))

                    ModernTextField(value = correo, onValueChange = { correo = it }, label = "Correo", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    Spacer(Modifier.height(12.dp))

                    ModernTextField(value = pass, onValueChange = { pass = it }, label = "Contraseña", visualTransformation = PasswordVisualTransformation())
                    Spacer(Modifier.height(12.dp))

                    ModernTextField(value = confirmPass, onValueChange = { confirmPass = it }, label = "Confirmar Contraseña", visualTransformation = PasswordVisualTransformation())
                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(1f)) {
                            ModernTextField(value = region, onValueChange = { region = it }, label = "Región")
                        }
                        Box(Modifier.weight(1f)) {
                            ModernTextField(value = comuna, onValueChange = { comuna = it }, label = "Comuna")
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = BluePrimary)
                    } else {
                        ModernButton(
                            text = "Registrarse",
                            onClick = {
                                viewModel.registrar(nombre, correo, pass, confirmPass, region, comuna)
                            }
                        )
                    }

                    errorMessage?.let {
                        Spacer(Modifier.height(16.dp))
                        Text(it, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
                    }
                    successMessage?.let {
                        Spacer(Modifier.height(16.dp))
                        Text(it, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("¿Ya tienes cuenta? ", color = TextGray)
                        TextButton(onClick = onNavigateToLogin) {
                            Text("Inicia Sesión", color = BluePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
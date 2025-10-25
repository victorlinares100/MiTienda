package Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mitienda.Rol
import com.example.mitienda.UserRepository

@Composable
fun LoginScreen(onLoginSuccess: (Rol) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") } //
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        // Campo de Email/Usuario
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email o Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Campo de Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Botón de Login
        Button(
            onClick = {
                // Aquí usamos password.trim() para pasarlo a authenticate, que lo mapea a 'contraseña'
                val user = UserRepository.authenticate(email.trim(), password.trim())
                if (user != null) {
                    errorMessage = null
                    // El user.role utiliza el Rol (ADMIN/CLIENT) que definiste.
                    onLoginSuccess(user.role)
                } else {
                    errorMessage = "Usuario o contraseña incorrectos Intentelo de nuevo "
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Ingresar")
        }

        // Mensaje de Error
        errorMessage?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
package Screens

import ViewModel.ProductViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mitienda.theme.BluePrimary

@Composable
fun AdminUserScreen(viewModel: ProductViewModel) {

    // Obtenemos la lista filtrada directamente del ViewModel
    val users = viewModel.getFilteredUsers()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            // --- BUSCADOR ---
            OutlinedTextField(
                value = viewModel.userSearchQuery,
                onValueChange = { viewModel.userSearchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre o email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    cursorColor = BluePrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- LISTA DE USUARIOS ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    UserCard(
                        user = user,
                        onClick = { viewModel.selectedUserForDetail = user }
                    )
                }
            }
        }

        // --- DIÁLOGO DE DETALLE ---
        if (viewModel.selectedUserForDetail != null) {
            UserDetailDialog(
                user = viewModel.selectedUserForDetail!!,
                onDismiss = { viewModel.selectedUserForDetail = null }
            )
        }
    }
}

// TARJETA DE LA LISTA
@Composable
fun UserCard(user: Model.User, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de perfil genérico
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // USAMOS LOS MÉTODOS SEGUROS AQUÍ
                Text(
                    text = user.getNombreSeguro(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = user.getEmailSeguro(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Manejo seguro del ID para el color del estado
            val safeId = user.id ?: 0L
            val isActive = safeId % 2 != 0L

            Badge(
                containerColor = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                contentColor = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
            ) {
                Text(if (isActive) "Activo" else "Bloqueado", modifier = Modifier.padding(4.dp))
            }
        }
    }
}

// VENTANA EMERGENTE CON EL DETALLE
@Composable
fun UserDetailDialog(user: Model.User, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(48.dp), tint = BluePrimary)
        },
        // Usamos el nombre seguro en el título
        title = { Text(text = user.getNombreSeguro()) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider()

                // Usamos el email seguro
                DetailRow(Icons.Default.Email, "Email", user.getEmailSeguro())

                // Datos simulados (puedes cambiarlos luego si vienen de la BD)
                DetailRow(Icons.Default.Phone, "Teléfono", "+56 9 .... ....")
                DetailRow(Icons.Default.LocationOn, "Región", user.region ?: "Sin región")
                DetailRow(Icons.Default.History, "Rol", user.rolString ?: "Cliente")

                Spacer(modifier = Modifier.height(16.dp))

                Text("Acciones de cuenta:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bloquear")
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset Pass")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        },
        containerColor = Color.White
    )
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
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

    val users = viewModel.getFilteredUsers()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
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

            Badge(
                containerColor = Color(0xFFE8F5E9),
                contentColor = Color(0xFF2E7D32)
            ) {
                Text("Activo", modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
fun UserDetailDialog(user: Model.User, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(48.dp), tint = BluePrimary)
        },
        title = { Text(text = user.getNombreSeguro()) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider()

                DetailRow(Icons.Default.Email, "Email", user.getEmailSeguro())
                DetailRow(Icons.Default.LocationOn, "Región", user.region ?: "Sin región")
                DetailRow(Icons.Default.Place, "Comuna", user.comuna ?: "Sin comuna")
                DetailRow(Icons.Default.Security, "Rol", user.rolString ?: "Cliente")
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
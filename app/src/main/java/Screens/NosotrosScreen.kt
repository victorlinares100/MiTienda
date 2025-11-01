package Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NosotrosScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Nuestra Historia",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Áurea nació con la idea de ofrecer ropa moderna, cómoda y accesible " +
                        "para quienes buscan expresar su estilo personal. Comenzamos como un pequeño " +
                        "proyecto y hoy seguimos creciendo gracias a la confianza de nuestros clientes.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Misión",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Nuestra misión es entregar prendas de calidad, inspiradas en las últimas tendencias, " +
                        "que combinen diseño y comodidad para el día a día.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Visión",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Queremos ser reconocidos como una marca cercana, confiable y con estilo, " +
                        "llevando la moda urbana a todas las personas que buscan marcar la diferencia.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Valores",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("• Calidad en cada prenda", style = MaterialTheme.typography.bodyLarge)
                Text("• Compromiso con nuestros clientes", style = MaterialTheme.typography.bodyLarge)
                Text("• Diseño innovador", style = MaterialTheme.typography.bodyLarge)
                Text("• Sostenibilidad y responsabilidad", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "“Vestimos tu actitud. Inspiramos tu estilo.”",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

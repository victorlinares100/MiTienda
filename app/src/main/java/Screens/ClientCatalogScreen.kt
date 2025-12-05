package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ViewModel.ProductViewModel
import Model.Product
import androidx.compose.runtime.collectAsState
import com.example.mitienda.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCatalogScreen(viewModel: ProductViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val allProducts = uiState.productList
    val categorias = uiState.categorias

    var selectedCategoryId: Long? by remember { mutableStateOf(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts: List<Product> = remember(allProducts, selectedCategoryId, searchQuery) {
        allProducts.filter { product ->
            val matchesCategory = selectedCategoryId == null || product.category?.id == selectedCategoryId
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    // Fondo gris claro
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F7FA)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar productos...", color = TextGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = BluePrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = InputBorder,
                        focusedContainerColor = Color(0xFFF5F7FA),
                        unfocusedContainerColor = Color(0xFFF5F7FA),
                        cursorColor = BluePrimary, // El palito que parpadea
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryPill(
                        text = "Todos",
                        isSelected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null }
                    )

                    categorias.forEach { category ->
                        CategoryPill(
                            text = category.nombre,
                            isSelected = selectedCategoryId == category.id,
                            onClick = { selectedCategoryId = category.id }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredProducts.size} Resultados",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextGray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filtrar",
                    tint = TextGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else {
                ProductList(
                    products = filteredProducts,
                    isClientView = true,
                    onAddToCart = { product -> viewModel.addToCart(product) }
                )
            }
        }
    }
}

@Composable
fun CategoryPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = if (isSelected) BluePrimary else InputBorder,
                shape = RoundedCornerShape(50)
            ),
        color = if (isSelected) BluePrimary else Color.White,
        contentColor = if (isSelected) Color.White else TextGray
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
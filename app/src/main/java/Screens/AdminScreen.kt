package Screens

import Model.*
import Utils.FileUtils
import ViewModel.ProductViewModel
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mitienda.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreen( // 1. CAMBIÉ EL NOMBRE AQUÍ
    viewModel: ProductViewModel
    // 2. QUITÉ onLogout (Lo maneja el Dashboard)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- TUS VARIABLES DE ESTADO INTACTAS ---
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("") }

    var selectedCatId by remember { mutableStateOf<Long?>(null) }
    var selectedBrandId by remember { mutableStateOf<Long?>(null) }
    var selectedSizeId by remember { mutableStateOf<Long?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }
    var editingProductId by remember { mutableStateOf<Long?>(null) }

    // --- TU LÓGICA DE IMAGEN INTACTA ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // --- TUS VARIABLES DE DROPDOWN INTACTAS ---
    var catExpanded by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }
    var sizeExpanded by remember { mutableStateOf(false) }

    fun clearForm() {
        name = ""; priceText = ""; stockText = ""
        selectedCatId = null; selectedBrandId = null; selectedSizeId = null
        selectedImageUri = null; currentImageUrl = null
        editingProductId = null
    }

    fun loadProductForEdit(product: Product) {
        editingProductId = product.id
        name = product.name
        priceText = product.price.toString()
        stockText = product.stock.toString()
        selectedCatId = product.category?.id
        selectedBrandId = product.brand?.id
        selectedSizeId = product.size?.id
        currentImageUrl = product.image?.url
        selectedImageUri = null
    }

    // 3. QUITÉ EL SCAFFOLD. LA COLUMNA AHORA ES LA RAÍZ.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp) // Padding interno
    ) {
        // --- TU MISMO CÓDIGO DE TARJETA Y FORMULARIO ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(if (editingProductId == null) "Nuevo Producto" else "Editar Producto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BluePrimary, cursorColor = BluePrimary)
                    )
                    OutlinedTextField(
                        value = stockText, onValueChange = { stockText = it },
                        label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BluePrimary, cursorColor = BluePrimary)
                    )
                }

                OutlinedTextField(
                    value = priceText, onValueChange = { priceText = it },
                    label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BluePrimary, cursorColor = BluePrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- TUS DROPDOWNS (SELECTORES) ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // Categoría
                    Box(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                        val catName = uiState.categorias.find { it.id == selectedCatId }?.nombre ?: "Categoría"
                        OutlinedButton(
                            onClick = { catExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = if (catExpanded || selectedCatId != null) SolidColor(BluePrimary) else SolidColor(InputBorder)
                            )
                        ) {
                            Text(catName, maxLines = 1)
                        }
                        DropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                            uiState.categorias.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat.nombre) }, onClick = { selectedCatId = cat.id; catExpanded = false })
                            }
                        }
                    }

                    // Marca
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                        val brandName = uiState.marcas.find { it.id == selectedBrandId }?.nombre ?: "Marca"
                        OutlinedButton(
                            onClick = { brandExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = if (brandExpanded || selectedBrandId != null) SolidColor(BluePrimary) else SolidColor(InputBorder)
                            )
                        ) {
                            Text(brandName, maxLines = 1)
                        }
                        DropdownMenu(expanded = brandExpanded, onDismissRequest = { brandExpanded = false }) {
                            uiState.marcas.forEach { brand ->
                                DropdownMenuItem(text = { Text(brand.nombre) }, onClick = { selectedBrandId = brand.id; brandExpanded = false })
                            }
                        }
                    }

                    // Talla
                    Box(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                        val sizeName = uiState.tallas.find { it.id == selectedSizeId }?.nombre ?: "Talla"
                        OutlinedButton(
                            onClick = { sizeExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = if (sizeExpanded || selectedSizeId != null) SolidColor(BluePrimary) else SolidColor(InputBorder)
                            )
                        ) {
                            Text(sizeName, maxLines = 1)
                        }
                        DropdownMenu(expanded = sizeExpanded, onDismissRequest = { sizeExpanded = false }) {
                            uiState.tallas.forEach { size ->
                                DropdownMenuItem(text = { Text(size.nombre) }, onClick = { selectedSizeId = size.id; sizeExpanded = false })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Imagen")
                    }

                    Spacer(Modifier.width(16.dp))

                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Preview",
                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else if (currentImageUrl != null) {
                        AsyncImage(
                            model = currentImageUrl,
                            contentDescription = "Current",
                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("Sin imagen", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val price = priceText.toDoubleOrNull()
                        val stock = stockText.toIntOrNull()

                        if (name.isNotBlank() && price != null && stock != null &&
                            selectedCatId != null && selectedBrandId != null && selectedSizeId != null) {

                            val imageFile = selectedImageUri?.let { FileUtils.getFileFromUri(context, it) }

                            if (editingProductId == null) {
                                viewModel.addProduct(name, price, stock, selectedCatId!!, selectedBrandId!!, selectedSizeId!!, imageFile)
                                Toast.makeText(context, "Creando producto...", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.updateProduct(editingProductId!!, name, price, stock, selectedCatId!!, selectedBrandId!!, selectedSizeId!!, imageFile, currentImageUrl)
                                Toast.makeText(context, "Actualizando...", Toast.LENGTH_SHORT).show()
                            }
                            clearForm()
                        } else {
                            Toast.makeText(context, "Faltan campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    else Text(if (editingProductId == null) "Agregar Producto" else "Guardar Cambios")
                }

                if (editingProductId != null) {
                    TextButton(onClick = { clearForm() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancelar Edición", color = TextGray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading && uiState.productList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BluePrimary) }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.productList) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = product.image?.url ?: "https://via.placeholder.com/150",
                                contentDescription = null,
                                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = BlueDarkBackground)
                                Text("$${product.price} - Stock: ${product.stock}", style = MaterialTheme.typography.bodyMedium, color = BluePrimary)
                                Text("${product.category?.nombre} | ${product.brand?.nombre} | ${product.size?.nombre}", style = MaterialTheme.typography.bodySmall, color = TextGray)
                            }

                            IconButton(onClick = { loadProductForEdit(product) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = BluePrimary)
                            }
                            IconButton(onClick = { viewModel.deleteProduct(product) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = ErrorRed)
                            }
                        }
                    }
                }
            }
        }
    }
}
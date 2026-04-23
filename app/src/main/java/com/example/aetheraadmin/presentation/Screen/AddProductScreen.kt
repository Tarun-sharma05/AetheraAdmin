package com.example.aetheraadmin.presentation.Screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.aetheraadmin.domain.models.Product
import com.example.aetheraadmin.presentation.category.CategoryViewModel
import com.example.aetheraadmin.presentation.product.AddProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    addProductViewModel: AddProductViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    innerPadding: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {}
) {
    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val addState by addProductViewModel.uiState.collectAsState()
    val categoryState by categoryViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var finalPrice by remember { mutableStateOf("") }
    var stockQuantity by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Validation states
    var nameError by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf("") }
    var categoryError by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf("") }
    var stockError by remember { mutableStateOf("") }
    var imageError by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) imageUri = uri
    }

    // When image upload completes → submit product
    LaunchedEffect(addState.imageUploadUrl) {
        if (addState.imageUploadUrl.isNotBlank() && isSubmitting) {
            addProductViewModel.addProduct(
                Product(
                    name          = name,
                    description   = description,
                    price         = price.toDoubleOrNull() ?: 0.0,
                    categoryName  = categoryName,
                    finalPrice    = finalPrice.toDoubleOrNull() ?: 0.0,
                    imageUrl      = addState.imageUploadUrl,
                    stockQuantity = stockQuantity.toIntOrNull() ?: 0
                )
            )
            isSubmitting = false
        }
    }

    LaunchedEffect(addState.success) {
        if (addState.success.isNotBlank()) {
            Toast.makeText(context, addState.success, Toast.LENGTH_SHORT).show()
            name = ""; description = ""; price = ""; categoryName = ""
            finalPrice = ""; stockQuantity = ""; imageUri = null; isSubmitting = false
            addProductViewModel.resetState()
        }
    }

    LaunchedEffect(addState.error) {
        if (addState.error.isNotBlank()) {
            isSubmitting = false
            Toast.makeText(context, addState.error, Toast.LENGTH_SHORT).show()
        }
    }

    fun validate(): Boolean {
        var isValid = true
        nameError        = if (name.isBlank()) "Product name is required" else ""
        descriptionError = if (description.isBlank()) "Description is required" else ""
        priceError       = when {
            price.isBlank()             -> "Price is required"
            price.toDoubleOrNull() == null -> "Enter a valid price"
            else                        -> ""
        }
        categoryError = if (categoryName.isBlank()) "Please select a category" else ""
        stockError    = when {
            stockQuantity.isBlank()          -> "Stock quantity is required"
            stockQuantity.toIntOrNull() == null -> "Enter a valid number"
            else                             -> ""
        }
        imageError = if (imageUri == null) "Please select a product image" else ""
        if (nameError.isNotBlank() || descriptionError.isNotBlank() || priceError.isNotBlank() ||
            categoryError.isNotBlank() || stockError.isNotBlank() || imageError.isNotBlank()) {
            isValid = false
        }
        return isValid
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Product", maxLines = 1, overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehaviour
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Image picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = if (imageError.isNotBlank()) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri, contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap to add image")
                    }
                }
            }
            if (imageError.isNotBlank()) {
                Text(imageError, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = name, onValueChange = { name = it; if (it.isNotBlank()) nameError = "" },
                label = { Text("Product Name") }, isError = nameError.isNotBlank(),
                supportingText = { if (nameError.isNotBlank()) Text(nameError, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.width(intrinsicSize = IntrinsicSize.Min)) {
                OutlinedTextField(
                    value = price, onValueChange = { price = it; if (it.isNotBlank()) priceError = "" },
                    label = { Text("Price") }, isError = priceError.isNotBlank(),
                    supportingText = { if (priceError.isNotBlank()) Text(priceError, color = MaterialTheme.colorScheme.error) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                OutlinedTextField(
                    value = finalPrice, onValueChange = { finalPrice = it },
                    label = { Text("Final Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = description, onValueChange = { description = it; if (it.isNotBlank()) descriptionError = "" },
                label = { Text("Description") }, isError = descriptionError.isNotBlank(),
                supportingText = { if (descriptionError.isNotBlank()) Text(descriptionError, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Category dropdown
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = categoryName, onValueChange = {},
                    isError = categoryError.isNotBlank(),
                    supportingText = { if (categoryError.isNotBlank()) Text(categoryError, color = MaterialTheme.colorScheme.error) },
                    readOnly = true, label = { Text("Category") },
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (categoryState.categories.isEmpty()) {
                        DropdownMenuItem(text = { Text("No categories found") }, onClick = { expanded = false })
                    } else {
                        categoryState.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = { categoryName = cat.name; expanded = false }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = stockQuantity, onValueChange = { stockQuantity = it },
                label = { Text("Stock Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            val isLoading = addState.isLoading || addState.isUploadingImage || isSubmitting
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick = {
                    if (!validate()) return@Button
                    if (imageUri != null && addState.imageUploadUrl.isBlank()) {
                        isSubmitting = true
                        addProductViewModel.uploadImage(imageUri!!)
                    } else if (addState.imageUploadUrl.isNotBlank()) {
                        isSubmitting = true
                        addProductViewModel.addProduct(
                            Product(
                                name          = name,
                                description   = description,
                                price         = price.toDoubleOrNull() ?: 0.0,
                                categoryName  = categoryName,
                                finalPrice    = finalPrice.toDoubleOrNull() ?: 0.0,
                                imageUrl      = addState.imageUploadUrl,
                                stockQuantity = stockQuantity.toIntOrNull() ?: 0
                            )
                        )
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Add Product")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
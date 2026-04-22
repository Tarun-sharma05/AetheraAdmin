package com.example.aetheraadmin.presentation.Screen

import android.R
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.aetheraadmin.domain.models.ProductsModels
import com.example.aetheraadmin.domain.models.category
import com.example.aetheraadmin.presentation.AppViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AppViewModel = hiltViewModel(),
    innerPadding: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {}
) {
    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    LaunchedEffect(key1 = true) {
        viewModel.getCategory()
    }

    val uploadProductImage = viewModel.uploadProductImageState.collectAsState()


    val GetCategoryState = viewModel.getCategoryState.collectAsState()
    val AddProductState = viewModel.addProductState.collectAsState()

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var finalPrice by remember { mutableStateOf("") }
    var availableUnits by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var createdBy by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    //validation state variables
    var nameError by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf("") }
    var categoryError by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf("") }
    var availableUnitsError by remember { mutableStateOf("") }
    var imageError by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { it ->
        if(it != null) {
            imageUri = it
            imageUrl = "" // Reset imageUrl if a new image is picked
        }

    }

    LaunchedEffect(uploadProductImage.value) {
        if (uploadProductImage.value.success.isNotBlank()) {
            imageUrl = uploadProductImage.value.success
            if (isSubmitting) {
                val data = ProductsModels(
                    name = name,
                    description = description,
                    price = price.toDoubleOrNull() ?: 0.0,
                    category = category,
                    finalPrice = finalPrice.toDoubleOrNull() ?: 0.0,
                    imageUri = imageUrl,
                    availableUnits = availableUnits.toIntOrNull() ?: 0,
                    createdBy = createdBy
                )
                viewModel.addProduct(data)
                isSubmitting = false
            }
        } else if (uploadProductImage.value.error.isNotBlank() && isSubmitting) {
            isSubmitting = false
            Toast.makeText(context, "Upload failed: ${uploadProductImage.value.error}", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(AddProductState.value.success) {
   if(        AddProductState.value.success.isNotBlank() ) {
        Toast.makeText(context, AddProductState.value.success, Toast.LENGTH_SHORT).show()
        name = ""
        description = ""
        price =  ""     //Double
        category = ""
        finalPrice = ""
        availableUnits = ""
        imageUrl = ""
        imageUri = null
        createdBy = ""
        isSubmitting = false
        viewModel.resetAddProductState()
    } }


    // Handle error
    LaunchedEffect(AddProductState.value.error) {
        if (AddProductState.value.error.isNotBlank()) {
            isSubmitting = false
            Toast.makeText(context, AddProductState.value.error, Toast.LENGTH_SHORT).show()
        }
    }



    fun validate(): Boolean {
        var isValid = true

        if (name.isBlank()) {
            nameError = "Product name is required"
            isValid = false
        } else nameError = ""

        if (description.isBlank()) {
            descriptionError = "Description is required"
            isValid = false
        } else descriptionError = ""

        if (price.isBlank()) {
            priceError = "Price is required"
            isValid = false
        } else if (price.toDoubleOrNull() == null) {
            priceError = "Enter a valid price"
            isValid = false
        } else priceError = ""

        if (category.isBlank()) {
            categoryError = "Please select a category"
            isValid = false
        } else categoryError = ""

        if (availableUnits.isBlank()) {
            availableUnitsError = "Available units is required"
            isValid = false
        } else if (availableUnits.toIntOrNull() == null) {
            availableUnitsError = "Enter a valid number"
            isValid = false
        } else availableUnitsError = ""

        if (imageUri == null) {
            imageError = "Please select a product image"
            isValid = false
        } else imageError = ""

        return isValid
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Product",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
                .padding(scaffoldPadding)             // ← respects TopAppBar height
                .padding(horizontal = 16.dp),
        ) {
//            Text(
//                text = "Add Product",
//                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier.align(Alignment.Start)
//            )
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            if (imageUri != null) {
//                AsyncImage(
//                    model = imageUri,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp)
//                        .clip(RoundedCornerShape(8.dp)),
//                    contentScale = ContentScale.Crop
//                )
//            } else {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .clip(RoundedCornerShape(8.dp)),
//                    contentAlignment = Alignment.Center,
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clickable {
//                                launcher.launch(
//                                    PickVisualMediaRequest(
//                                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
//                                    )
//                                )
//                            }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = null,
//                            modifier = Modifier.clickable {
//                                launcher.launch(
//                                    PickVisualMediaRequest(
//                                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
//                                    )
//                                )
//                            })
//                        Text(text = "Add Image")
//                    }
//                }
//
//                // Image — show error below the image Box
//                if (imageError.isNotBlank()) {
//                    Text(
//                        text = imageError,
//                        color = MaterialTheme.colorScheme.error,
//                        style = MaterialTheme.typography.bodySmall,
//                        modifier = Modifier
//                            .align(Alignment.Start)
//                            .padding(start = 16.dp, top = 4.dp)
//                    )
//                }
//            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = if (imageError.isNotBlank())
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        launcher.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Tap to add image")
                    }
                }
            }

// Error sits outside the Box, always in the same position
            if (imageError.isNotBlank()) {
                Text(
                    text = imageError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))


            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotBlank()) {
                        nameError = ""
                    }  // clears error as user types
                },
                label = { Text(text = "Product Name") },
                isError = nameError.isNotBlank(),
                supportingText = {
                    if (nameError.isNotBlank()) {
                        Text(text = nameError, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.width(intrinsicSize = IntrinsicSize.Min)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        if (it.isNotBlank()) {
                            priceError = ""
                        }
                    },
                    label = { Text(text = "Price") },
                    isError = priceError.isNotBlank(),
                    supportingText = {
                        if (priceError.isNotBlank()) {
                            Text(text = priceError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))

                OutlinedTextField(
                    value = finalPrice,
                    onValueChange = { finalPrice = it },
                    label = { Text(text = "Final Price") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    if (it.isNotBlank()) {
                        descriptionError = ""
                    }
                },
                label = { Text(text = "Description") },
                isError = descriptionError.isNotBlank(),
                supportingText = {
                    if (descriptionError.isNotBlank()) {
                        Text(text = descriptionError, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    isError = categoryError.isNotBlank(),
                    supportingText = {
                        if (categoryError.isNotBlank()) {
                            Text(text = categoryError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    readOnly = true,
                    label = { Text(text = "Category") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {

                    val categories = GetCategoryState.value.success ?: emptyList()

                    if (categories.isEmpty()) {
                        DropdownMenuItem(

                            text = { Text("No categories found") },
                            onClick = {

                                expanded = false
                            }
                        )
                    } else {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(text = cat.name) },
                                onClick = {
                                    Log.d("CAT_DEBUG", "Full object: $cat")
                                    Log.d("CAT_DEBUG", "cat.name value: '${cat.name}'")
                                    category = cat.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = availableUnits,
                onValueChange = { availableUnits = it },
                label = { Text(text = "Available Units") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = createdBy,
                onValueChange = { createdBy = it },
                label = { Text(text = "Created By") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            val isLoading = AddProductState.value.isLoading || uploadProductImage.value.isLoading || isSubmitting
            
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick = {
                    if (!validate()) return@Button   // stops here if any field is invalid

                    if (imageUri != null && imageUrl.isBlank()) {
                        isSubmitting = true
                        viewModel.uploadProductImage(imageUri!!)
                    } else if (imageUrl.isNotBlank()) {
                        isSubmitting = true
                        val data = ProductsModels(
                            name = name,
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0,
                            category = category,
                            finalPrice = finalPrice.toDoubleOrNull() ?: 0.0,
                            imageUri = imageUrl,
                            availableUnits = availableUnits.toIntOrNull() ?: 0,
                            createdBy = createdBy
                        )
                        viewModel.addProduct(data)
                    }

                }) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,  // white spinner on black button
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(text = "Add Product")
                }
            }
        }
    }
}




@Composable
fun AddProductScreenContent() {

}





@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    AddProductScreen()
}
package com.example.aetheraadmin.presentation.Screen

import android.R
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.aetheraadmin.presentation.AddProductState
import com.example.aetheraadmin.presentation.AppViewModel
import kotlin.contracts.contract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(modifier: Modifier = Modifier, viewModel: AppViewModel = hiltViewModel()) {

    LaunchedEffect(key1 = true) {
        viewModel.getCategory()
    }

    val uploadProductImage = viewModel.uploadProductImageState.collectAsState()


    val getCategoryState = viewModel.getCategoryState.collectAsState()
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


    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
        if(it != null) {
            viewModel.uploadProductImage(it)
            imageUri = it
        }

    }


    when{
        AddProductState.value.isLoading -> {
            Box(
              modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }

        uploadProductImage.value.success != null -> {
            imageUrl = uploadProductImage.value.success
        }

        AddProductState.value.success.isNotBlank() -> {
            Toast.makeText(context, AddProductState.value.success, Toast.LENGTH_SHORT).show()
            name = ""
            description = ""
            price = ""
            category = ""
            finalPrice = ""
            availableUnits = ""
            imageUrl = ""
            imageUri = null
            createdBy = ""

            viewModel.resetAddProductState()
        }

        AddProductState.value.error.isNotBlank() -> {
            Toast.makeText(context, AddProductState.value.error, Toast.LENGTH_SHORT).show()
        }


    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {

        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }else{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ){
               Column (
                   horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.Center,
                   modifier = Modifier.fillMaxSize().clickable{
                          PickVisualMediaRequest(
                              mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                          )
                   }
               ){
                   Icon(
                       imageVector = Icons.Default.Add,
                       contentDescription = null,
                       modifier = Modifier.clickable{})
                   Text(text = "Add Image")
               }
            }

        }

        Text(
            text = "Add Product",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Start)
        )




    }

}
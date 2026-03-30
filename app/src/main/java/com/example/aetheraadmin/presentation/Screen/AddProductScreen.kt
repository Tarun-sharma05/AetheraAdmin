package com.example.aetheraadmin.presentation.Screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aetheraadmin.presentation.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(modifier: Modifier = Modifier, viewModel: AppViewModel = hiltViewModel()) {

    LaunchedEffect(key1 = true) {
        viewModel.getCategory()
    }

    val uploadProductImage = viewModel.uploadProductImageState.collectAsState()


    val getCategoryState = viewModel.getCategoryState.collectAsState()
    val addProductState = viewModel.addProductState.collectAsState()

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
        imageUri = it

    }

}
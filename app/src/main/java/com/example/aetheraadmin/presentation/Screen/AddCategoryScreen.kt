package com.example.aetheraadmin.presentation.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import com.example.aetheraadmin.domain.models.Category
import com.example.aetheraadmin.presentation.category.AddCategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    viewModel: AddCategoryViewModel = hiltViewModel(),
    innerPadding: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var categoryName by remember { mutableStateOf("") }
    var categoryImage by remember { mutableStateOf("") }

    LaunchedEffect(state.success) {
        if (state.success.isNotBlank()) {
            Toast.makeText(context, state.success, Toast.LENGTH_SHORT).show()
            categoryName = ""
            categoryImage = ""
            viewModel.resetState()
        }
    }

    LaunchedEffect(state.error) {
        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Category", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text(text = "Category Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = categoryImage,
                onValueChange = { categoryImage = it },
                label = { Text(text = "Category Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                onClick = {
                    if (categoryName.isBlank()) {
                        Toast.makeText(context, "Category name is required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.addCategory(Category(name = categoryName, imageUrl = categoryImage))
                }
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.padding(4.dp)
                    )
                } else {
                    Text(text = "Add Category")
                }
            }
        }
    }
}

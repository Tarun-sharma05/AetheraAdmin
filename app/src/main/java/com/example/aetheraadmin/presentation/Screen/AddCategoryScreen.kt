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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aetheraadmin.domain.models.category
import com.example.aetheraadmin.presentation.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    viewModel: AppViewModel = hiltViewModel(),
    innerPadding: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {}
) {
    val state = viewModel.addCategoryState.collectAsState()
    val categoryName  = remember { mutableStateOf("") }
    val categoryImage = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Category", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
                value = categoryName.value,
                onValueChange = { categoryName.value = it },
                label = { Text(text = "Category Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = categoryImage.value,
                onValueChange = { categoryImage.value = it },
                label = { Text(text = "Category Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val data = category(
                        name = categoryName.value,
                        imageUri = categoryImage.value
                    )
                    viewModel.addCategory(data)
                }
            ) {
                Text(text = "Add Category")
            }

            if (state.value.success.isNotBlank()) {
                Text(text = "✅ ${state.value.success}")
            }
            if (state.value.error.isNotBlank()) {
                Text(text = "❌ ${state.value.error}")
            }
        }
    }
}

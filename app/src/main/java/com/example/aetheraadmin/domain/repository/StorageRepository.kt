package com.example.aetheraadmin.domain.repository

import android.net.Uri
import com.example.aetheraadmin.common.ResultState
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun uploadImage(imageUri: Uri, path: String = "Products"): Flow<ResultState<String>>
}

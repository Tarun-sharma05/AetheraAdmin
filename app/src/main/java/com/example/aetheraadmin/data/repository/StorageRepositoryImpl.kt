package com.example.aetheraadmin.data.repository

import android.net.Uri
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.repository.StorageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) : StorageRepository {

    override fun uploadImage(imageUri: Uri, path: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val ref = storage.reference.child("$path/${System.currentTimeMillis()}")
        ref.putFile(imageUri)
            .addOnSuccessListener { task ->
                task.storage.downloadUrl
                    .addOnSuccessListener { url -> trySend(ResultState.Success(url.toString())) }
                    .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to get download URL")) }
                if (task.error != null) {
                    trySend(ResultState.Error(task.error!!.message ?: "Upload error"))
                }
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Image upload failed")) }
        awaitClose { close() }
    }
}

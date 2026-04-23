package com.example.aetheraadmin.data.repository

import com.example.aetheraadmin.common.CATEGORY
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.Category
import com.example.aetheraadmin.domain.repository.CategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    override fun addCategory(category: Category): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(CATEGORY).add(category)
            .addOnSuccessListener { trySend(ResultState.Success("Category added successfully")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to add category")) }
        awaitClose { close() }
    }

    override fun getCategories(): Flow<ResultState<List<Category>>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(CATEGORY).get()
            .addOnSuccessListener { snapshot ->
                val categories = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Category::class.java)?.copy(id = doc.id)
                }
                trySend(ResultState.Success(categories))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to load categories")) }
        awaitClose { close() }
    }

    override fun getCategoryById(id: String): Flow<ResultState<Category?>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(CATEGORY).document(id).get()
            .addOnSuccessListener { doc ->
                val category = doc.toObject(Category::class.java)?.copy(id = doc.id)
                trySend(ResultState.Success(category))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Category not found")) }
        awaitClose { close() }
    }

    override fun updateCategory(category: Category): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(CATEGORY).document(category.id)
            .set(category.copy(updatedAt = System.currentTimeMillis()))
            .addOnSuccessListener { trySend(ResultState.Success("Category updated successfully")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to update category")) }
        awaitClose { close() }
    }

    override fun deleteCategory(id: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(CATEGORY).document(id).delete()
            .addOnSuccessListener { trySend(ResultState.Success("Category deleted")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to delete category")) }
        awaitClose { close() }
    }
}

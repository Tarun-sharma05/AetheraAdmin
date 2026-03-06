package com.example.aetheraadmin.data.repoimpl

import androidx.compose.ui.layout.RectRulers
import com.example.aetheraadmin.common.CATEGORY
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.category
import com.example.aetheraadmin.domain.models.products
import com.example.aetheraadmin.domain.repo.repo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class repoImpal @Inject constructor(private val FirebaseFirestore : FirebaseFirestore): repo {

    override fun addCategory(category: category): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        FirebaseFirestore.collection(CATEGORY).add(category).addOnSuccessListener {
            trySend(ResultState.Success("Category Added Successfully"))
        }.addOnFailureListener {
             trySend(ResultState.Error(it.toString()))
        }
        awaitClose{
            close()
        }

    }

    override fun getCategory(): Flow<ResultState<List<category>>> = callbackFlow {
        trySend(ResultState.Loading)
            FirebaseFirestore.collection(CATEGORY)
                .get()
                .addOnSuccessListener { querySnapshot ->

                    val categories = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(category::class.java)
                    }
                    trySend(ResultState.Success(categories))
                }
                .addOnFailureListener { exception ->
                    trySend(ResultState.Error(exception.toString()))
                }
               awaitClose { close() }

    }

    override fun addProduct(products: products): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        FirebaseFirestore.collection("PRODUCTS").add(products).addOnSuccessListener {
            trySend(ResultState.Success("Product Added Successfully"))

        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()
        }
    }
}
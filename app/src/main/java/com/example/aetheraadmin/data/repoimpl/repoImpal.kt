package com.example.aetheraadmin.data.repoimpl

import com.example.aetheraadmin.common.CATEGORY
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.category
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
}
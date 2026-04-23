package com.example.aetheraadmin.data.repository

import com.example.aetheraadmin.common.USERS
import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.User
import com.example.aetheraadmin.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun getUsers(): Flow<ResultState<List<User>>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(USERS).get()
            .addOnSuccessListener { snap ->
                val users = snap.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.copy(userId = doc.id)
                }
                trySend(ResultState.Success(users))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "Failed to load users")) }
        awaitClose { close() }
    }

    override fun getUserById(userId: String): Flow<ResultState<User?>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(USERS).document(userId).get()
            .addOnSuccessListener { doc ->
                trySend(ResultState.Success(doc.toObject(User::class.java)?.copy(userId = doc.id)))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message ?: "User not found")) }
        awaitClose { close() }
    }
}

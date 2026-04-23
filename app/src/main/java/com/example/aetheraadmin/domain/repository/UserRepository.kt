package com.example.aetheraadmin.domain.repository

import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<ResultState<List<User>>>
    fun getUserById(userId: String): Flow<ResultState<User?>>
}

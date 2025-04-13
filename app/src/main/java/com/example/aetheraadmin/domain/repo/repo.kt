package com.example.aetheraadmin.domain.repo

import com.example.aetheraadmin.common.ResultState
import com.example.aetheraadmin.domain.models.category
import kotlinx.coroutines.flow.Flow

interface repo {

    fun addCategory(category: category): Flow<ResultState<String>>
}
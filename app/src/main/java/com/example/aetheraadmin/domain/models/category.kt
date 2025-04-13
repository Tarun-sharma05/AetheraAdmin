package com.example.aetheraadmin.domain.models

import java.util.Date

data class category (
    var name: String   = "",
    val date: Long = System.currentTimeMillis(),
    var imageUri: String = "",
    )
package com.example.aetheraadmin.domain.models


data class products (
  var name: String = "",
  var description: String = "",
  var price: Double = 0.0,
  var category: String = "",
  var finalPrice: Double = 0.0,
  var imageUri: String = "",
  var date: Long = System.currentTimeMillis(),
  var availableUnits: Int = 0,
  var createdBy: String = ""
    )
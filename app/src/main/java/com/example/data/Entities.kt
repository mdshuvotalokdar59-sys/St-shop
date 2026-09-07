package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val imageUrl: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productName: String,
    val price: Double,
    val customerName: String,
    val phone: String,
    val address: String,
    val paymentMethod: String,
    val dateFormatted: String,
    val timestamp: Long = System.currentTimeMillis()
)

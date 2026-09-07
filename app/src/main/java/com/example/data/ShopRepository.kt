package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ShopRepository(
    private val productDao: ProductDao,
    private val orderDao: OrderDao
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun ensureDefaultProducts() = withContext(Dispatchers.IO) {
        if (productDao.getProductCount() == 0) {
            productDao.insertAll(AppDatabase.defaultProducts)
        }
    }

    suspend fun addProduct(name: String, price: Double, imageUrl: String) = withContext(Dispatchers.IO) {
        productDao.insertProduct(
            ProductEntity(
                name = name.trim(),
                price = price,
                imageUrl = imageUrl.trim()
            )
        )
    }

    suspend fun deleteProduct(id: Long) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(id)
    }

    suspend fun placeOrder(
        productName: String,
        price: Double,
        customerName: String,
        phone: String,
        address: String,
        paymentMethod: String,
        dateFormatted: String
    ) = withContext(Dispatchers.IO) {
        orderDao.insertOrder(
            OrderEntity(
                productName = productName.trim(),
                price = price,
                customerName = customerName.trim(),
                phone = phone.trim(),
                address = address.trim(),
                paymentMethod = paymentMethod.trim(),
                dateFormatted = dateFormatted.trim(),
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteOrder(id: Long) = withContext(Dispatchers.IO) {
        orderDao.deleteOrder(id)
    }
}

package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ProductEntity::class, OrderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shopping_store_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate initial products from HTML template
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            database.productDao().insertAll(defaultProducts)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        val defaultProducts = listOf(
            ProductEntity(
                name = "Premium T-Shirt",
                price = 650.0,
                imageUrl = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab"
            ),
            ProductEntity(
                name = "Stylish Shoes",
                price = 1200.0,
                imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff"
            ),
            ProductEntity(
                name = "Smart Watch",
                price = 1800.0,
                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30"
            )
        )
    }
}

package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.OrderEntity
import com.example.data.ProductEntity
import com.example.data.ShopRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    HOME,
    ADMIN
}

data class OrderFormState(
    val customerName: String = "",
    val customerPhone: String = "",
    val customerAddress: String = "",
    val paymentMethod: String = "Cash on Delivery",
    val isSubmitting: Boolean = false
)

data class AddProductFormState(
    val name: String = "",
    val price: String = "",
    val imageUrl: String = ""
)

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShopRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = ShopRepository(database.productDao(), database.orderDao())
        viewModelScope.launch {
            repository.ensureDefaultProducts()
        }
    }

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedProductForOrder = MutableStateFlow<ProductEntity?>(null)
    val selectedProductForOrder: StateFlow<ProductEntity?> = _selectedProductForOrder.asStateFlow()

    private val _orderForm = MutableStateFlow(OrderFormState())
    val orderForm: StateFlow<OrderFormState> = _orderForm.asStateFlow()

    private val _addProductForm = MutableStateFlow(AddProductFormState())
    val addProductForm: StateFlow<AddProductFormState> = _addProductForm.asStateFlow()

    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    private val _productToDelete = MutableStateFlow<ProductEntity?>(null)
    val productToDelete: StateFlow<ProductEntity?> = _productToDelete.asStateFlow()

    fun navigateToAdmin() {
        _currentScreen.value = AppScreen.ADMIN
    }

    fun navigateToHome() {
        _currentScreen.value = AppScreen.HOME
    }

    fun openOrderModal(product: ProductEntity) {
        _selectedProductForOrder.value = product
        _orderForm.value = OrderFormState()
    }

    fun closeOrderModal() {
        _selectedProductForOrder.value = null
        _orderForm.value = OrderFormState()
    }

    fun updateCustomerName(name: String) {
        _orderForm.value = _orderForm.value.copy(customerName = name)
    }

    fun updateCustomerPhone(phone: String) {
        _orderForm.value = _orderForm.value.copy(customerPhone = phone)
    }

    fun updateCustomerAddress(address: String) {
        _orderForm.value = _orderForm.value.copy(customerAddress = address)
    }

    fun updatePaymentMethod(method: String) {
        _orderForm.value = _orderForm.value.copy(paymentMethod = method)
    }

    fun submitOrder() {
        val product = _selectedProductForOrder.value ?: return
        val form = _orderForm.value

        if (form.customerName.isBlank() || form.customerPhone.isBlank() || form.customerAddress.isBlank() || form.paymentMethod.isBlank()) {
            viewModelScope.launch {
                _messageEvent.emit("সব তথ্য পূরণ করুন!")
            }
            return
        }

        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault())
            val dateStr = dateFormat.format(Date())

            repository.placeOrder(
                productName = product.name,
                price = product.price,
                customerName = form.customerName,
                phone = form.customerPhone,
                address = form.customerAddress,
                paymentMethod = form.paymentMethod,
                dateFormatted = dateStr
            )

            closeOrderModal()
            _messageEvent.emit("✅ আপনার অর্ডার সফলভাবে গ্রহণ করা হয়েছে!")
        }
    }

    fun updateProductName(name: String) {
        _addProductForm.value = _addProductForm.value.copy(name = name)
    }

    fun updateProductPrice(price: String) {
        _addProductForm.value = _addProductForm.value.copy(price = price)
    }

    fun updateProductImageUrl(url: String) {
        _addProductForm.value = _addProductForm.value.copy(imageUrl = url)
    }

    fun setSampleImageUrl(url: String) {
        _addProductForm.value = _addProductForm.value.copy(imageUrl = url)
    }

    fun addProduct() {
        val form = _addProductForm.value
        val name = form.name.trim()
        val priceDouble = form.price.trim().toDoubleOrNull()
        val image = form.imageUrl.trim()

        if (name.isBlank() || priceDouble == null || priceDouble <= 0 || image.isBlank()) {
            viewModelScope.launch {
                _messageEvent.emit("সব তথ্য পূরণ করুন!")
            }
            return
        }

        viewModelScope.launch {
            repository.addProduct(name, priceDouble, image)
            _addProductForm.value = AddProductFormState()
            _messageEvent.emit("✅ প্রোডাক্ট যোগ হয়েছে!")
        }
    }

    fun requestDeleteProduct(product: ProductEntity) {
        _productToDelete.value = product
    }

    fun dismissDeleteProduct() {
        _productToDelete.value = null
    }

    fun confirmDeleteProduct() {
        val product = _productToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteProduct(product.id)
            _productToDelete.value = null
            _messageEvent.emit("প্রোডাক্ট মুছে ফেলা হয়েছে")
        }
    }

    fun deleteOrder(id: Long) {
        viewModelScope.launch {
            repository.deleteOrder(id)
            _messageEvent.emit("অর্ডার মুছে ফেলা হয়েছে")
        }
    }
}

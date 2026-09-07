package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandNavy
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ShopApp(
    viewModel: ShopViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProductForOrder.collectAsStateWithLifecycle()
    val orderForm by viewModel.orderForm.collectAsStateWithLifecycle()
    val addProductForm by viewModel.addProductForm.collectAsStateWithLifecycle()
    val productToDelete by viewModel.productToDelete.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Modal dialog for placing order
    if (selectedProduct != null) {
        OrderDialog(
            product = selectedProduct!!,
            formState = orderForm,
            onNameChange = viewModel::updateCustomerName,
            onPhoneChange = viewModel::updateCustomerPhone,
            onAddressChange = viewModel::updateCustomerAddress,
            onPaymentMethodChange = viewModel::updatePaymentMethod,
            onSubmit = viewModel::submitOrder,
            onDismiss = viewModel::closeOrderModal
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StoreHeader(
                currentScreen = currentScreen,
                onNavigateToAdmin = viewModel::navigateToAdmin,
                onNavigateToHome = viewModel::navigateToHome
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { screen ->
            when (screen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        products = products,
                        onProductClick = viewModel::openOrderModal,
                        onNavigateToAdmin = viewModel::navigateToAdmin
                    )
                }
                AppScreen.ADMIN -> {
                    AdminScreen(
                        formState = addProductForm,
                        products = products,
                        orders = orders,
                        productToDelete = productToDelete,
                        onNameChange = viewModel::updateProductName,
                        onPriceChange = viewModel::updateProductPrice,
                        onImageUrlChange = viewModel::updateProductImageUrl,
                        onSelectSamplePreset = { name, price, url ->
                            viewModel.updateProductName(name)
                            viewModel.updateProductPrice(price)
                            viewModel.updateProductImageUrl(url)
                        },
                        onAddProduct = viewModel::addProduct,
                        onRequestDeleteProduct = viewModel::requestDeleteProduct,
                        onConfirmDeleteProduct = viewModel::confirmDeleteProduct,
                        onDismissDeleteProduct = viewModel::dismissDeleteProduct,
                        onDeleteOrder = viewModel::deleteOrder,
                        onNavigateToHome = viewModel::navigateToHome
                    )
                }
            }
        }
    }
}

@Composable
fun StoreHeader(
    currentScreen: AppScreen,
    onNavigateToAdmin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Surface(
        color = BrandNavy,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🛍️ My Shopping Store",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 20.sp
            )

            if (currentScreen == AppScreen.HOME) {
                Button(
                    onClick = onNavigateToAdmin,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_panel_button")
                ) {
                    Text(
                        text = "Admin Panel",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Button(
                    onClick = onNavigateToHome,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("home_header_button")
                ) {
                    Text(
                        text = "Home",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

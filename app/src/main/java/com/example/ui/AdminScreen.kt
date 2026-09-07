package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.data.OrderEntity
import com.example.data.ProductEntity
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.BrandRed
import com.example.ui.theme.BrandTextSecondary

@Composable
fun AdminScreen(
    formState: AddProductFormState,
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    productToDelete: ProductEntity?,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onSelectSamplePreset: (name: String, price: String, url: String) -> Unit,
    onAddProduct: () -> Unit,
    onRequestDeleteProduct: (ProductEntity) -> Unit,
    onConfirmDeleteProduct: () -> Unit,
    onDismissDeleteProduct: () -> Unit,
    onDeleteOrder: (Long) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Delete Confirmation Dialog
    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = onDismissDeleteProduct,
            title = {
                Text(
                    text = "প্রোডাক্ট ডিলিট",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = "এই প্রোডাক্টটি (${productToDelete.name}) মুছে ফেলবেন?")
            },
            confirmButton = {
                Button(
                    onClick = onConfirmDeleteProduct,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandRed),
                    modifier = Modifier.testTag("confirm_delete_product_button")
                ) {
                    Text("হ্যাঁ, মুছুন")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissDeleteProduct,
                    modifier = Modifier.testTag("cancel_delete_product_button")
                ) {
                    Text("বাতিল করুন")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Back to Home Button
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onNavigateToHome,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                modifier = Modifier.testTag("back_to_home_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "← Home Page", color = Color.White)
            }
        }

        // Section 1: Add Product Form
        item {
            AddProductSection(
                formState = formState,
                onNameChange = onNameChange,
                onPriceChange = onPriceChange,
                onImageUrlChange = onImageUrlChange,
                onSelectSamplePreset = onSelectSamplePreset,
                onAddProduct = onAddProduct
            )
        }

        // Section 2: Inventory List
        item {
            InventorySection(
                products = products,
                onDeleteClick = onRequestDeleteProduct
            )
        }

        // Section 3: Customer Orders List
        item {
            OrdersSection(
                orders = orders,
                onDeleteOrder = onDeleteOrder
            )
        }

        // Footer spacing
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AddProductSection(
    formState: AddProductFormState,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onSelectSamplePreset: (name: String, price: String, url: String) -> Unit,
    onAddProduct: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_add_product_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📦", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inventory Admin Panel",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets for quick autofill on mobile
            Text(
                text = "দ্রুত নমুনা সিলেক্ট করুন (Preset):",
                style = MaterialTheme.typography.labelMedium,
                color = BrandTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {
                        onSelectSamplePreset(
                            "Wireless Headphones",
                            "2500",
                            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e"
                        )
                    },
                    label = { Text("Headphones") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                AssistChip(
                    onClick = {
                        onSelectSamplePreset(
                            "Leather Backpack",
                            "3200",
                            "https://images.unsplash.com/photo-1553062407-98eeb64c6a62"
                        )
                    },
                    label = { Text("Backpack") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                AssistChip(
                    onClick = {
                        onSelectSamplePreset(
                            "Classic Sunglasses",
                            "950",
                            "https://images.unsplash.com/photo-1511499767150-a48a237f0083"
                        )
                    },
                    label = { Text("Sunglasses") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Product Name
            OutlinedTextField(
                value = formState.name,
                onValueChange = onNameChange,
                label = { Text("প্রোডাক্টের নাম") },
                placeholder = { Text("উদাহরণ: T-Shirt, Shoes...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_product_name_input"),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Product Price
            OutlinedTextField(
                value = formState.price,
                onValueChange = onPriceChange,
                label = { Text("প্রোডাক্টের দাম (৳)") },
                placeholder = { Text("উদাহরণ: 750") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_product_price_input"),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Product Image URL
            OutlinedTextField(
                value = formState.imageUrl,
                onValueChange = onImageUrlChange,
                label = { Text("ছবির URL") },
                placeholder = { Text("https://...") },
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_product_image_input"),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Product Button
            Button(
                onClick = onAddProduct,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_add_product_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "+ প্রোডাক্ট যোগ করুন",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun InventorySection(
    products: List<ProductEntity>,
    onDeleteClick: (ProductEntity) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_inventory_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🛍️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Inventory",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "মোট: ${products.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (products.isEmpty()) {
                Text(
                    text = "কোনো প্রোডাক্ট নেই।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandTextSecondary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    products.forEachIndexed { index, product ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SubcomposeAsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.name,
                                contentScale = ContentScale.Crop,
                                error = {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.DarkGray)
                                    }
                                },
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "৳ ${product.price.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BrandGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { onDeleteClick(product) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandRed),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("delete_product_${product.id}")
                            ) {
                                Text(text = "Delete", color = Color.White, fontSize = 13.sp)
                            }
                        }

                        if (index < products.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersSection(
    orders: List<OrderEntity>,
    onDeleteOrder: (Long) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_orders_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📋", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Customer Orders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "মোট: ${orders.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (orders.isEmpty()) {
                Text(
                    text = "এখনো কোনো অর্ডার আসেনি।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandTextSecondary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    orders.forEach { order ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🛍️ ${order.productName}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    IconButton(
                                        onClick = { onDeleteOrder(order.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "অর্ডার মুছুন",
                                            tint = BrandRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Customer: ${order.customerName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Phone: ${order.phone}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Address: ${order.address}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Price: ৳ ${order.price.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Method: ${order.paymentMethod}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = order.dateFormatted,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

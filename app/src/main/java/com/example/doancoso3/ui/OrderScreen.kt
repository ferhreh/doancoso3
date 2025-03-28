package com.example.doancoso3.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doancoso3.model.Order
import com.example.doancoso3.viewmodel.OrderViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Done
// Enum class để lưu trạng thái đơn hàng
enum class OrderStatus(val value: Int, val label: String) {
    PROCESSING(1, "Đang xử lý"),
    DELIVERED(2, "Đã giao"),
    CANCELLED(3, "Đã hủy")
}

@Composable
fun OrderScreen(navController: NavController, userId: Int, orderViewModel: OrderViewModel) {
    // State để lưu trạng thái đang chọn
    val (selectedStatus, setSelectedStatus) = remember { mutableStateOf(OrderStatus.PROCESSING) }

    // State để lưu danh sách đơn hàng và cập nhật UI khi có thay đổi
    var orders by remember { mutableStateOf(orderViewModel.getOrdersByUserId(userId)) }

    // CoroutineScope để xử lý các tác vụ bất đồng bộ
    val coroutineScope = rememberCoroutineScope()

    // Trạng thái để kiểm tra xem placeOrderWithAutoUpdate đã hoàn tất chưa
    val isOrderPlaced by remember { orderViewModel.orderPlaced }

    // Lọc đơn hàng theo trạng thái đã chọn
    val filteredOrders = remember(selectedStatus, orders) {
        orders.filter { it.status == selectedStatus.value }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tiêu đề
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Đơn hàng", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row chứa 3 card trạng thái
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Card Đang xử lý
                StatusCard(
                    status = OrderStatus.PROCESSING,
                    isSelected = selectedStatus == OrderStatus.PROCESSING,
                    onClick = { setSelectedStatus(OrderStatus.PROCESSING) }
                )

                // Card Đã giao
                StatusCard(
                    status = OrderStatus.DELIVERED,
                    isSelected = selectedStatus == OrderStatus.DELIVERED,
                    onClick = { setSelectedStatus(OrderStatus.DELIVERED) }
                )

                // Card Đã hủy
                StatusCard(
                    status = OrderStatus.CANCELLED,
                    isSelected = selectedStatus == OrderStatus.CANCELLED,
                    onClick = { setSelectedStatus(OrderStatus.CANCELLED) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Box hiển thị danh sách đơn hàng
        if (filteredOrders.isEmpty()) {
            // Hiển thị khi không có đơn hàng nào
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "No Orders",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Không có đơn hàng nào",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Hiển thị danh sách đơn hàng
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                items(filteredOrders.groupBy { it.orderDate }.entries.toList()) { (orderDate, ordersInDate) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        // Hiển thị ngày đặt hàng
                        Text(
                            text = "Ngày đặt: $orderDate",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Hiển thị từng đơn hàng trong ngày
                        ordersInDate.forEach { order ->
                            OrderItem(
                                order = order,
                                orderViewModel = orderViewModel,
                                isOrderPlaced = isOrderPlaced,
                                onStatusChanged = {
                                    // Làm mới danh sách đơn hàng khi trạng thái thay đổi
                                    coroutineScope.launch {
                                        delay(100) // Delay nhỏ để đảm bảo DB đã cập nhật
                                        orders = orderViewModel.getOrdersByUserId(userId)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun StatusCard(
    status: OrderStatus,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(40.dp)
            .clickable { onClick() },
        elevation = 4.dp,
        backgroundColor = if (isSelected) MaterialTheme.colors.primary else Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = status.label,
                color = if (isSelected) Color.White else Color.Black,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun OrderItem(
    order: Order,
    orderViewModel: OrderViewModel,
    isOrderPlaced: Boolean = false,
    onStatusChanged: () -> Unit = {},
    onDelivered: () -> Unit = {}
) {
    val shippingFee = 30000.0
    val canMarkAsDelivered = order.status == OrderStatus.PROCESSING.value &&
            (isOrderPlaced || orderViewModel.isOrderAutoDeliverable(order))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Dòng 1: Tên sản phẩm và trạng thái
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                val statusText = when (order.status) {
                    1 -> "Đang xử lý"
                    2 -> "Đã giao"
                    3 -> "Đã hủy"
                    else -> "Không xác định"
                }

                val statusColor = when (order.status) {
                    1 -> Color.Blue
                    2 -> Color.Green
                    3 -> Color.Red
                    else -> Color.Gray
                }

                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dòng 2: Số lượng và đơn giá
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Số lượng: ${order.soLuong}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Đơn giá: ${formatCurrency(order.productPrice)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dòng 3: Địa chỉ giao hàng
            Text(
                text = "Địa chỉ: ${order.address}",
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dòng 4: Phương thức thanh toán và vận chuyển
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Thanh toán: ${order.paymentMethod}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Vận chuyển: ${order.deliveryMethod}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Phí vận chuyển:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = formatCurrency(shippingFee),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Dòng 5: Tổng tiền
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Tổng tiền: ${formatCurrency(order.totalAmount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Hiển thị các nút dựa trên trạng thái đơn hàng
            if (order.status == OrderStatus.PROCESSING.value) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Nút Đã giao (chỉ hiện khi đã hoàn tất đặt hàng)
                    if (canMarkAsDelivered) {
                        Button(
                            onClick = {
                                // Cập nhật trạng thái sang Đã giao
                                orderViewModel.updateOrderStatus(order.id, OrderStatus.DELIVERED.value)
                                onStatusChanged()
                                onDelivered()
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (orderViewModel.isOrderAutoDeliverable(order)) Color.Green else Color.Gray
                            ),
                            enabled = orderViewModel.isOrderAutoDeliverable(order),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Đã giao",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Đã giao",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Nút Hủy đơn
                    Button(
                        onClick = {
                            // Hủy đơn hàng và cập nhật trạng thái
                            orderViewModel.updateOrderStatus(order.id, OrderStatus.CANCELLED.value)
                            onStatusChanged()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Hủy đơn",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (order.status == OrderStatus.DELIVERED.value) {
                // Nút Đánh giá cho đơn hàng đã giao
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // TODO: Implement rating functionality
                            // Chuyển đến màn hình đánh giá sản phẩm
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Đánh giá",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
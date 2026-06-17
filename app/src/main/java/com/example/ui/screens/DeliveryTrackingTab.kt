package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun DeliveryTrackingTab(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    var subTabState by remember { mutableStateOf(0) } // 0 = Parcel Delivery, 1 = GPS Tracking

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector bar
            TabRow(
                selectedTabIndex = subTabState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[subTabState]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = subTabState == 0,
                    onClick = { subTabState = 0 },
                    text = { Text(viewModel.translate("parcel_delivery"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = null) },
                    modifier = Modifier.testTag("parcel_tab")
                )
                Tab(
                    selected = subTabState == 1,
                    onClick = { subTabState = 1 },
                    text = { Text(viewModel.translate("gps_tracking"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.PinDrop, contentDescription = null) },
                    modifier = Modifier.testTag("gps_tab")
                )
            }

            if (subTabState == 0) {
                ParcelDeliveryTab(viewModel = viewModel)
            } else {
                GpsTrackingTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ParcelDeliveryTab(viewModel: AppViewModel) {
    val context = LocalContext.current

    // Sender & Recipient fields
    var senderName by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }
    var senderLoc by remember { mutableStateOf("Dhaka") }
    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var recipientLoc by remember { mutableStateOf("Barishal") }

    var parcelWeight by remember { mutableStateOf(1.0) }
    var selectedCargoType by remember { mutableStateOf("Box Cargo") }
    var sizeCategory by remember { mutableStateOf("Medium Box") }

    // Auto estimated pricing calculation
    val baseRate = 90.0
    val weightMultiplier = 35.0
    val estimatedFare = baseRate + (parcelWeight * weightMultiplier)

    // Prepopulate info if user is authenticated
    val u by viewModel.currentUser.collectAsState()
    LaunchedEffect(u) {
        if (u.isLogged && senderName.isBlank()) {
            senderName = u.name
            senderPhone = u.phone
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().widthIn(max = 550.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (viewModel.isBangla) "আপনার পার্সেল বুকিং সাবমিট করুন" else "Order Express Courier & Cargo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // Sender Layout Group
                Text(
                    text = if (viewModel.isBangla) "প্রেরক (Sender)" else "Sender Outpost Details",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = senderName,
                    onValueChange = { senderName = it },
                    label = { Text(viewModel.translate("name")) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = senderPhone,
                        onValueChange = { senderPhone = it },
                        label = { Text(viewModel.translate("phone")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1.2f).padding(end = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = senderLoc,
                        onValueChange = { senderLoc = it },
                        label = { Text(viewModel.translate("search_hint_from")) },
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray.copy(alpha = 0.15f))

                // Recipient Layout Group
                Text(
                    text = if (viewModel.isBangla) "প্রাপক (Recipient)" else "Recipient Delivery Address",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    label = { Text(if (viewModel.isBangla) "প্রাপকের সম্পূর্ণ নাম" else "Recipient Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = recipientPhone,
                        onValueChange = { recipientPhone = it },
                        label = { Text(viewModel.translate("phone")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1.2f).padding(end = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = recipientLoc,
                        onValueChange = { recipientLoc = it },
                        label = { Text(viewModel.translate("search_hint_to")) },
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray.copy(alpha = 0.15f))

                // Cargo Metrics
                Text(
                    text = viewModel.translate("parcel_type"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Row of types
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Documents", "Box Cargo", "Fragile / Glass", "Liquid Care").forEach { cargo ->
                        val active = selectedCargoType == cargo
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.15f))
                                .clickable { selectedCargoType = cargo },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cargo,
                                fontSize = 10.sp,
                                color = if (active) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Weight Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = viewModel.translate("weight"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "${String.format("%.1f", parcelWeight)} Kg", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = parcelWeight.toFloat(),
                    onValueChange = { parcelWeight = it.toDouble() },
                    valueRange = 0.1f..15f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Live estimated cost panel
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = viewModel.translate("estimated_cost"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = "${viewModel.translate("taka_unit")}${String.format("%.0f", estimatedFare)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (senderName.isBlank() || recipientName.isBlank() || senderPhone.isBlank() || recipientPhone.isBlank()) {
                            Toast.makeText(context, "Please complete fields", Toast.LENGTH_SHORT).show()
                        } else {
                            val err = viewModel.submitParcelDelivery(
                                senderName, senderPhone, senderLoc,
                                recipientName, recipientPhone, recipientLoc,
                                parcelWeight, selectedCargoType, sizeCategory, estimatedFare
                            )
                            if (err != null) {
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Booking recorded successfully! Courier dispatch initialized.", Toast.LENGTH_LONG).show()
                                recipientName = ""
                                recipientPhone = ""
                                parcelWeight = 1.0
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("dispatch_parcel_btn"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = viewModel.translate("order_parcel"), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GpsTrackingTab(viewModel: AppViewModel) {
    // We animate a shuttle traveling along the Dhaka-Padma Bridge-Bhanga highway!
    var distanceFraction by remember { mutableStateOf(0.0f) }
    var currentSpeed by remember { mutableStateOf(78) }
    var currentCheckpoint by remember { mutableStateOf("Dhaka Central Outpost") }
    var remainingMinutes by remember { mutableStateOf(52) }

    // Highway stops checkpoints
    val milestones = listOf(
        Milestone("Dhaka (Postogola)", 0.0f, "12:00 PM"),
        Milestone("Dhaleshwari Toll Gate", 0.25f, "12:12 PM"),
        Milestone("Mawa Expressway Center", 0.5f, "12:26 PM"),
        Milestone("Padma Bridge Toll Plaza", 0.65f, "12:35 PM"),
        Milestone("Janjira Gateway", 0.85f, "12:44 PM"),
        Milestone("Bhanga Hub, Faridpur", 1.0f, "12:52 PM")
    )

    // Animation Loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            distanceFraction += 0.02f
            if (distanceFraction > 1.0f) {
                distanceFraction = 0.0f
            }

            // Fluctuating speed
            currentSpeed = Random.nextInt(72, 94)

            // Determine check point
            val currentMilestone = milestones.firstOrNull { it.fraction >= distanceFraction } ?: milestones.last()
            currentCheckpoint = currentMilestone.name

            // Calc remaining time
            remainingMinutes = ((1.0f - distanceFraction) * 55).toInt()
        }
    }

    val currentLatitude = 23.7115 + (distanceFraction * (23.3820 - 23.7115))
    val currentLongitude = 90.4125 + (distanceFraction * (89.9836 - 90.4125))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().widthIn(max = 550.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = viewModel.translate("tracking_text"),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Animation Canvas: The route
                val pathColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                val activePathColor = MaterialTheme.colorScheme.primary
                val dotColor = MaterialTheme.colorScheme.tertiary

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 10f
                        val widthCoord = size.width
                        val heightCoord = size.height / 2.0f

                        // Draw baseline highway trail
                        drawLine(
                            color = pathColor,
                            start = Offset(0f, heightCoord),
                            end = Offset(widthCoord, heightCoord),
                            strokeWidth = strokeW
                        )

                        // Draw completed highway trail
                        drawLine(
                            color = activePathColor,
                            start = Offset(0f, heightCoord),
                            end = Offset(widthCoord * distanceFraction, heightCoord),
                            strokeWidth = strokeW
                        )

                        // Draw milestone points on highway
                        milestones.forEach { mill ->
                            val spotX = widthCoord * mill.fraction
                            drawCircle(
                                color = if (distanceFraction >= mill.fraction) activePathColor else Color.Gray,
                                radius = 7f,
                                center = Offset(spotX, heightCoord)
                            )
                        }

                        // Draw the active animated vehicle shuttle dot!
                        val shuttleX = widthCoord * distanceFraction
                        drawCircle(
                            color = dotColor,
                            radius = 12f,
                            center = Offset(shuttleX, heightCoord)
                        )
                    }

                    // Milestone layout texts inside canvas
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Dhaka",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                        Text(
                            text = "Padma Bridge",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                        Text(
                            text = "Bhanga",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // GPS Coordinate info Cards
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        TrackingDataRow(viewModel.translate("current_position"), currentCheckpoint)
                        TrackingDataRow("GPS Coordinates", String.format("%.5f° N, %.5f° E", currentLatitude, currentLongitude))
                        TrackingDataRow(viewModel.translate("speed"), "$currentSpeed km/h")
                        TrackingDataRow(viewModel.translate("eta"), if (remainingMinutes > 0) "$remainingMinutes mins" else "Arrived")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detailed milestones log list
                Text(
                    text = if (viewModel.isBangla) "এক্সপ্রেসওয়ে রোডম্যাপ মাইলস্টোন:" else "Expressway Milestones logs:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                milestones.forEach { mill ->
                    val isPassed = distanceFraction >= mill.fraction
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isPassed) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = mill.name,
                                fontSize = 12.sp,
                                color = if (isPassed) MaterialTheme.colorScheme.onSurface else Color.Gray,
                                fontWeight = if (isPassed) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }

                        Text(
                            text = mill.estimatedTime,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackingDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = value, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

data class Milestone(val name: String, val fraction: Float, val estimatedTime: String)

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.geometry.Size
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardTab(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var adminSubTab by remember { mutableStateOf(0) } // 0 = Performance Stats, 1 = Create Schedule

    // New schedule forms
    var transType by remember { mutableStateOf(TransportType.BUS) }
    var companyName by remember { mutableStateOf("") }
    var routeFrom by remember { mutableStateOf("") }
    var routeTo by remember { mutableStateOf("") }
    var timeStart by remember { mutableStateOf("09:00 AM") }
    var timeEnd by remember { mutableStateOf("04:30 PM") }
    var tariffPrice by remember { mutableStateOf("") }
    var capacitySeats by remember { mutableStateOf("40") }
    var vehicleRegNo by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Tab switchers
            TabRow(
                selectedTabIndex = adminSubTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = adminSubTab == 0,
                    onClick = { adminSubTab = 0 },
                    text = { Text(viewModel.translate("admin_dashboard"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                )
                Tab(
                    selected = adminSubTab == 1,
                    onClick = { adminSubTab = 1 },
                    text = { Text(viewModel.translate("add_route"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.AddBusiness, contentDescription = null) },
                    modifier = Modifier.testTag("add_schedule_tab")
                )
            }

            if (adminSubTab == 0) {
                // SYSTEM STATS VIEW
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.translate("live_metrics"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Card Stats Grid
                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatCard(
                            label = viewModel.translate("ticket_sales"),
                            value = "${viewModel.translate("taka_unit")}${String.format("%.0f", viewModel.systemRevenue)}",
                            icon = Icons.Default.MonetizationOn,
                            tintColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        StatCard(
                            label = viewModel.translate("passenger_count"),
                            value = viewModel.totalRidersBooked.toString(),
                            icon = Icons.Default.Group,
                            tintColor = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatCard(
                            label = viewModel.translate("active_cars"),
                            value = "14 Channels",
                            icon = Icons.Default.EvStation,
                            tintColor = Colors.yellowTone,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        StatCard(
                            label = "Server Latency",
                            value = "24 ms",
                            icon = Icons.Default.NetworkCheck,
                            tintColor = Colors.blueTone,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divisional Ticket Sales graph (Canvas)
                    Text(
                        text = if (viewModel.isBangla) "বিভাগীয় টিকেট চাহিদা বাৎসরিক (৳):" else "Regional Volume Demands (৳ Yearly Avg):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Custom bar drawing
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                    val heightMax = size.height
                                    val widthMax = size.width
                                    val numBars = 5
                                    val barW = 50f
                                    val barGap = (widthMax - (numBars * barW)) / (numBars + 1)

                                    val divisions = listOf(
                                        BarData("Dhaka", 0.9f, BdGreenPrimary),
                                        BarData("Ctg", 0.75f, BdRedAccent),
                                        BarData("Sylhet", 0.6f, BdGoldAccent),
                                        BarData("Brs", 0.45f, Colors.blueTone),
                                        BarData("Khulna", 0.5f, Color.Gray)
                                    )

                                    divisions.forEachIndexed { idx, bar ->
                                        val xCoord = barGap + idx * (barW + barGap)
                                        val barHeight = heightMax * bar.weight * 0.9f
                                        val yCoord = heightMax - barHeight

                                        // Draw shadow
                                        drawRect(
                                            color = Color.LightGray.copy(alpha = 0.1f),
                                            topLeft = Offset(xCoord, 0f),
                                            size = Size(barW, heightMax)
                                        )

                                        // Draw filled Bar with rounded tops
                                        drawRect(
                                            color = bar.color,
                                            topLeft = Offset(xCoord, yCoord),
                                            size = Size(barW, barHeight)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Labels under the custom canvas bars
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf("Dhaka", "CTg", "Sylhet", "Barishal", "Khulna").forEach { div ->
                                    Text(
                                        text = div,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        modifier = Modifier.width(55.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // CREATE / INJECT VEHICLE SCHEDULER
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = if (viewModel.isBangla) "নতুন ট্রিপ নির্ধারণ ফরম" else "Launch New Transit Journey",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Mode selectors
                        Text(text = if (viewModel.isBangla) "পরিবহন মাধ্যম:" else "Transport Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TransportType.values().forEach { type ->
                                val active = transType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 2.dp)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.15f))
                                        .clickable { transType = type }
                                        .testTag("admin_type_${type.name}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.White else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text(if (viewModel.isBangla) "কোম্পানির নাম (যেমন: হানিফ)" else "Operator / Brand Name") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("admin_company_input"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = routeFrom,
                                onValueChange = { routeFrom = it },
                                label = { Text(viewModel.translate("search_hint_from")) },
                                modifier = Modifier.weight(1f).padding(end = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = routeTo,
                                onValueChange = { routeTo = it },
                                label = { Text(viewModel.translate("search_hint_to")) },
                                modifier = Modifier.weight(1f).padding(start = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                            OutlinedTextField(
                                value = timeStart,
                                onValueChange = { timeStart = it },
                                label = { Text(if (viewModel.isBangla) "ছাড়ার সময়" else "Dept. Time") },
                                modifier = Modifier.weight(1f).padding(end = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = timeEnd,
                                onValueChange = { timeEnd = it },
                                label = { Text(if (viewModel.isBangla) "পৌঁছানোর সময়" else "Arrival Time") },
                                modifier = Modifier.weight(1f).padding(start = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                            OutlinedTextField(
                                value = tariffPrice,
                                onValueChange = { tariffPrice = it },
                                label = { Text(if (viewModel.isBangla) "টিকিট মূল্য (৳)" else "Fare Tariff (৳)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).padding(end = 4.dp).testTag("admin_price_input"),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = capacitySeats,
                                onValueChange = { capacitySeats = it },
                                label = { Text(if (viewModel.isBangla) "সিটের ক্ষমতা" else "Seat Cap") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).padding(start = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = vehicleRegNo,
                            onValueChange = { vehicleRegNo = it },
                            label = { Text(if (viewModel.isBangla) "রেজিস্ট্রেশন বা রুট নং" else "Vehicle License Plate / ID") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val fare = tariffPrice.toDoubleOrNull()
                                val seats = capacitySeats.toIntOrNull()
                                if (companyName.isBlank() || routeFrom.isBlank() || routeTo.isBlank() || fare == null || seats == null || vehicleRegNo.isBlank()) {
                                    Toast.makeText(context, "Please enter all details correctly", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.addCustomSchedule(
                                        type = transType,
                                        company = companyName,
                                        from = routeFrom,
                                        to = routeTo,
                                        start = timeStart,
                                        end = timeEnd,
                                        fare = fare,
                                        seats = seats,
                                        code = vehicleRegNo
                                    )
                                    // Reset
                                    companyName = ""
                                    routeFrom = ""
                                    routeTo = ""
                                    tariffPrice = ""
                                    vehicleRegNo = ""
                                    Toast.makeText(context, "New Schedule Injected successfully!", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("admin_submit_schedule_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = viewModel.translate("add_route"), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Icon(imageVector = icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(18.dp))
            }

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

data class BarData(val division: String, val weight: Float, val color: Color)

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RideShareRentalTab(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    var subTabState by remember { mutableStateOf(0) } // 0 = Ride Sharing, 1 = Vehicle Rental
    var isPostFormExpanded by remember { mutableStateOf(false) }

    // Forms
    var shareFrom by remember { mutableStateOf("") }
    var shareTo by remember { mutableStateOf("") }
    var posterName by remember { mutableStateOf("") }
    var shareRate by remember { mutableStateOf("") }
    var sharePhone by remember { mutableStateOf("") }
    var selectedIsBike by remember { mutableStateOf(true) }

    // Rental Booking
    var showRentDialog by remember { mutableStateOf<RentalVehicle?>(null) }
    var rentDays by remember { mutableStateOf(1) }

    val rides by viewModel.rideShares.collectAsState()
    val vehicles by viewModel.vehicles.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // High level tabs
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
                    text = { Text(viewModel.translate("ride_sharing"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.TwoWheeler, contentDescription = null) },
                    modifier = Modifier.testTag("ride_tab")
                )
                Tab(
                    selected = subTabState == 1,
                    onClick = { subTabState = 1 },
                    text = { Text(viewModel.translate("vehicle_rental"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
                    modifier = Modifier.testTag("rental_tab")
                )
            }

            if (subTabState == 0) {
                // RIDE SHARING CONTENT
                Column(modifier = Modifier.fillMaxSize()) {
                    // "Share your Ride / Create Booking" toggle card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isPostFormExpanded = !isPostFormExpanded },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AddLocationAlt, "Add", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (viewModel.isBangla) "আপনার রাইড শেয়ার করুন" else "Share Your Ride / Offer Seats",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Icon(
                                    imageVector = if (isPostFormExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            AnimatedVisibility(visible = isPostFormExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = posterName,
                                        onValueChange = { posterName = it },
                                        label = { Text(viewModel.translate("name")) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                        OutlinedTextField(
                                            value = shareFrom,
                                            onValueChange = { shareFrom = it },
                                            label = { Text(viewModel.translate("search_hint_from")) },
                                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        OutlinedTextField(
                                            value = shareTo,
                                            onValueChange = { shareTo = it },
                                            label = { Text(viewModel.translate("search_hint_to")) },
                                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }

                                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                        OutlinedTextField(
                                            value = shareRate,
                                            onValueChange = { shareRate = it },
                                            label = { Text(if (viewModel.isBangla) "ভাড়ার হার (৳)" else "Seat Share Tariff (৳)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        OutlinedTextField(
                                            value = sharePhone,
                                            onValueChange = { sharePhone = it },
                                            label = { Text(viewModel.translate("phone")) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }

                                    // Vehicle Type Choice Checkboxes
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (viewModel.isBangla) "বাহন:" else "Vehicle Type: ",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(end = 12.dp)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { selectedIsBike = true }
                                        ) {
                                            RadioButton(selected = selectedIsBike, onClick = { selectedIsBike = true })
                                            Text(text = if (viewModel.isBangla) "বাইক" else "Motorbike", fontSize = 13.sp)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { selectedIsBike = false }
                                        ) {
                                            RadioButton(selected = !selectedIsBike, onClick = { selectedIsBike = false })
                                            Text(text = if (viewModel.isBangla) "প্রাইভেট কার" else "Sedan Car", fontSize = 13.sp)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            val cost = shareRate.toDoubleOrNull()
                                            if (shareFrom.isBlank() || shareTo.isBlank() || posterName.isBlank() || cost == null || sharePhone.isBlank()) {
                                                Toast.makeText(context, "Fill in all metrics", Toast.LENGTH_SHORT).show()
                                            } else {
                                                viewModel.postRideShare(
                                                    from = shareFrom,
                                                    to = shareTo,
                                                    driver = posterName,
                                                    price = cost,
                                                    isBike = selectedIsBike,
                                                    phone = sharePhone
                                                )
                                                // Reset
                                                shareFrom = ""
                                                shareTo = ""
                                                posterName = ""
                                                shareRate = ""
                                                sharePhone = ""
                                                isPostFormExpanded = false
                                                Toast.makeText(context, "Ride listed successfully!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp)
                                            .testTag("submit_ride_share_btn"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (viewModel.isBangla) "রাইড পোস্ট করুন" else "Offer Ride Share ➔")
                                    }
                                }
                            }
                        }
                    }

                    // Feed List of Drivers
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(rides) { ride ->
                            RideShareCard(ride = ride, viewModel = viewModel)
                        }
                    }
                }
            } else {
                // VEHICLE RENTAL CONTENT
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = if (viewModel.isBangla) "বাংলাদেশজুড়ে আধুনিক ও আরামদায়ক কার রেন্টাল সুবিধা" else "Comfortable & air-conditioned rental fleet for long trips",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(vehicles) { vehicle ->
                            RentalVehicleCard(
                                vehicle = vehicle,
                                viewModel = viewModel,
                                onRentClick = {
                                    showRentDialog = vehicle
                                    rentDays = 1
                                }
                            )
                        }
                    }
                }
            }
        }

        // Vehicle Rental Duration Confirm Custom dialog State
        if (showRentDialog != null) {
            val v = showRentDialog!!
            val totalCost = v.pricePerDay * rentDays

            AlertDialog(
                onDismissRequest = { showRentDialog = null },
                confirmButton = {
                    Button(
                        onClick = {
                            val err = viewModel.bookRental(v, rentDays)
                            if (err != null) {
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    if (viewModel.isBangla) "রেকর্ড করা হয়েছে! চালক ফোনে যোগাযোগ করবে।" else "Submited! Fleet driver will call you shortly.",
                                    Toast.LENGTH_LONG
                                ).show()
                                showRentDialog = null
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_rent_btn")
                    ) {
                        Text(viewModel.translate("confirm_bk"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRentDialog = null }) {
                        Text(if (viewModel.isBangla) "বন্ধ করুন" else "Cancel")
                    }
                },
                title = {
                    Text(
                        text = if (viewModel.isBangla) "গাড়ি বুকিং নিশ্চয়তা" else "Rent Booking Summary",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = v.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = v.type.name + "  |  " + v.fuelType,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = if (viewModel.isBangla) "ভাড়ার বিবরণী:" else "Tariff Costing Breakdown:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (viewModel.isBangla) "প্রতিদিনের ভাড়া:" else "Rate/Day:")
                            Text(text = "${viewModel.translate("taka_unit")}${v.pricePerDay}")
                        }

                        // Days Stepper
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (viewModel.isBangla) "ভাড়ার দিন সংখ্যা:" else "Duration (Days):", fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (rentDays > 1) rentDays-- }
                                ) {
                                    Icon(Icons.Default.RemoveCircleOutline, "Dec")
                                }
                                Text(
                                    text = rentDays.toString(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                IconButton(onClick = { rentDays++ }) {
                                    Icon(Icons.Default.AddCircleOutline, "Inc")
                                }
                            }
                        }

                        Divider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (viewModel.isBangla) "সর্বমোট টাকা:" else "Total Costing:",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${viewModel.translate("taka_unit")}$totalCost",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun RideShareCard(ride: RideShare, viewModel: AppViewModel) {
    val context = LocalContext.current
    val ratingStars = "★".repeat(ride.rating.toInt())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Driver info and cost
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (ride.isBike) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = ride.driverName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "$ratingStars  |  ${ride.timeAvailable}",
                            color = BdGoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "${viewModel.translate("taka_unit")}${ride.price}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.1f))

            // Cities
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TripOrigin, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = ride.routeFrom, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = ride.routeTo, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action: WhatsApp call or Dialer Simulation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Dialing driver ${ride.phoneNumber}...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (viewModel.isBangla) "ফোন করুন" else "Call Driver", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Redirecting to chat...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Chat, contentDescription = "Chat", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (viewModel.isBangla) "মেসেজ লিখুন" else "Accept Ride", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RentalVehicleCard(
    vehicle: RentalVehicle,
    viewModel: AppViewModel,
    onRentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = vehicle.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EventSeat, "Seats", tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${vehicle.seats} ${if (viewModel.isBangla) "আসন" else "Seats"}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.LocalGasStation, "Fuel", tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = vehicle.fuelType, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${viewModel.translate("taka_unit")}${vehicle.pricePerDay}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(text = if (viewModel.isBangla) "প্রতিদিন" else "/ per day", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = vehicle.description, fontSize = 12.sp, color = Color.Gray.copy(alpha = 0.9f))

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Driver status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (vehicle.driverIncluded) Icons.Default.VerifiedUser else Icons.Default.Engineering,
                        contentDescription = "Driver",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (vehicle.driverIncluded) {
                            if (viewModel.isBangla) "অভিজ্ঞ চালকসহ ভাড়া" else "Executive Driver Included"
                        } else {
                            if (viewModel.isBangla) "চালক ছাড়া ভাড়া (Self-Drive)" else "Self-Drive Vehicle Base"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = onRentClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = viewModel.translate("book_now"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

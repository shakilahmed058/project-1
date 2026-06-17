package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppViewModel
import com.example.data.BkgTicket
import com.example.data.TransportSchedule
import com.example.data.TransportType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportSearchTab(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schedules by viewModel.schedules.collectAsState()

    var showBookingSheet by remember { mutableStateOf<TransportSchedule?>(null) }
    var selectedSeats = remember { mutableStateListOf<String>() }
    var passengerName by remember { mutableStateOf("") }
    var passengerPhone by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf<BkgTicket?>(null) }

    val filteredList = remember(schedules, viewModel.searchFrom, viewModel.searchTo, viewModel.selectedCategory) {
        schedules.filter {
            it.type == viewModel.selectedCategory &&
            (viewModel.searchFrom.isBlank() || it.routeFrom.contains(viewModel.searchFrom, ignoreCase = true)) &&
            (viewModel.searchTo.isBlank() || it.routeTo.contains(viewModel.searchTo, ignoreCase = true))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Search Controls Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.translate("find_transport"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Origin & Destination swapping design
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = viewModel.searchFrom,
                                onValueChange = { viewModel.searchFrom = it },
                                label = { Text(viewModel.translate("search_hint_from")) },
                                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("from_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = viewModel.searchTo,
                                onValueChange = { viewModel.searchTo = it },
                                label = { Text(viewModel.translate("search_hint_to")) },
                                leadingIcon = { Icon(Icons.Default.Navigation, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("to_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Swap Button
                        IconButton(
                            onClick = {
                                val temp = viewModel.searchFrom
                                viewModel.searchFrom = viewModel.searchTo
                                viewModel.searchTo = temp
                            },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(50))
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Locations",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Transport Categories Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    TransportType.BUS to Icons.Default.DirectionsBus to "bus",
                    TransportType.TRAIN to Icons.Default.Train to "train",
                    TransportType.LAUNCH to Icons.Default.DirectionsBoat to "launch",
                    TransportType.FLIGHT to Icons.Default.Flight to "flight"
                ).forEach { (pair, keyLabel) ->
                    val (type, icon) = pair
                    val selected = viewModel.selectedCategory == type
                    
                    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.primary
                    val bgColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .height(55.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(bgColor)
                            .clickable { viewModel.selectedCategory = type }
                            .testTag("cat_tab_${keyLabel}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = keyLabel,
                                tint = contentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = viewModel.translate(keyLabel),
                                color = contentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transport Result Cards
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = "Empty",
                            tint = Color.Gray,
                            modifier = Modifier.size(70.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (viewModel.isBangla) "কোনো সার্ভিস খুঁজে পাওয়া যায়নি।" else "No services matches search constraints.",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { schedule ->
                        TransportCard(
                            schedule = schedule,
                            onBookClick = {
                                selectedSeats.clear()
                                selectedSeats.add("A1") // Default selected
                                passengerName = viewModel.currentUser.value.name
                                passengerPhone = viewModel.currentUser.value.phone
                                showBookingSheet = schedule
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }

        // Custom Bottom Sheet Dialog for Booking
        if (showBookingSheet != null) {
            val sched = showBookingSheet!!
            AlertDialog(
                onDismissRequest = { showBookingSheet = null },
                confirmButton = {
                    Button(
                        onClick = {
                            if (passengerName.isBlank() || passengerPhone.isBlank()) {
                                Toast.makeText(context, "Fill passenger details", Toast.LENGTH_SHORT).show()
                            } else {
                                val errorReason = viewModel.bookTicket(sched, passengerName, passengerPhone)
                                if (errorReason != null) {
                                    Toast.makeText(context, errorReason, Toast.LENGTH_LONG).show()
                                } else {
                                    // Succeeded! Show success coupon sheet
                                    val currentBookings = viewModel.activeBookings.value
                                    if (currentBookings.isNotEmpty()) {
                                        showSuccessDialog = currentBookings.first()
                                    }
                                    showBookingSheet = null
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_booking_btn")
                    ) {
                        Text(viewModel.translate("confirm_bk"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBookingSheet = null }) {
                        Text(if (viewModel.isBangla) "বন্ধ করুন" else "Cancel")
                    }
                },
                title = {
                    Text(
                        text = viewModel.translate("booking_dialog_title"),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${sched.companyName}  |  ${sched.routeFrom} ➔ ${sched.routeTo}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${viewModel.translate("taka_unit")}${sched.price}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = if (viewModel.isBangla) "আসন নির্বাচন করুন (১টি সংরক্ষিত)" else "Choose Seats (1 pre-reserved for you)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                        )

                        // Interactive seat grids
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            listOf("A1", "A2", "B1", "B2", "C1", "C2").forEach { seat ->
                                val selected = selectedSeats.contains(seat)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.2f))
                                        .clickable {
                                            if (selected) selectedSeats.remove(seat) else selectedSeats.add(seat)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = seat,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = passengerName,
                            onValueChange = { passengerName = it },
                            label = { Text(viewModel.translate("name")) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = passengerPhone,
                            onValueChange = { passengerPhone = it },
                            label = { Text(viewModel.translate("phone")) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            )
        }

        // Custom Ticket Success Coupon popup
        if (showSuccessDialog != null) {
            val tk = showSuccessDialog!!
            AlertDialog(
                onDismissRequest = { showSuccessDialog = null },
                confirmButton = {
                    Button(onClick = { showSuccessDialog = null }) {
                        Text(if (viewModel.isBangla) "টিকিট নিয়ে নিন" else "Done")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, "Success", tint = GreenSuccess, modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(viewModel.translate("booking_success"), color = GreenSuccess)
                    }
                },
                text = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "BOARDING COUPON",
                                fontSize = 11.sp,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TicketRow("Carrier", tk.companyName)
                            TicketRow("Passenger", tk.passengerName)
                            TicketRow("From", tk.from)
                            TicketRow("To", tk.to)
                            TicketRow("Seat Number", tk.seatNo)
                            TicketRow("Boarding PIN", tk.boardingCode)
                            TicketRow("Price", "${viewModel.translate("taka_unit")}${tk.price}")

                            Spacer(modifier = Modifier.height(16.dp))

                            // Draw a beautiful simulation QR code
                            Card(
                                modifier = Modifier
                                    .size(130.dp)
                                    .align(Alignment.CenterHorizontally),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode2,
                                            contentDescription = "QR Code",
                                            tint = Color.Black,
                                            modifier = Modifier.size(90.dp)
                                        )
                                        Text(
                                            text = tk.boardingCode,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TransportCard(
    schedule: TransportSchedule,
    onBookClick: () -> Unit,
    viewModel: AppViewModel
) {
    val isDark = viewModel.darkThemeOverride

    val categoryColor = when (schedule.type) {
        TransportType.BUS -> MaterialTheme.colorScheme.primary
        TransportType.TRAIN -> Colors.yellowTone
        TransportType.LAUNCH -> Colors.blueTone
        TransportType.FLIGHT -> MaterialTheme.colorScheme.tertiary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("schedule_card_${schedule.companyName}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Company Name & Type Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = schedule.companyName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                // Type Pill Badge
                Box(
                    modifier = Modifier
                        .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val icon = when (schedule.type) {
                            TransportType.BUS -> Icons.Default.DirectionsBus
                            TransportType.TRAIN -> Icons.Default.Train
                            TransportType.LAUNCH -> Icons.Default.DirectionsBoat
                            TransportType.FLIGHT -> Icons.Default.Flight
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = schedule.type.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // From / To visual flow
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.routeFrom,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Start: ${schedule.timeStart}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Arrow indicator
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.East,
                        contentDescription = "to",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = schedule.routeTo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "End: ${schedule.timeEnd}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.15f))

            // Seat counts, rating, price, and Book now
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left metrics: Seats and Rating stars
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EventSeat,
                            contentDescription = "Seat",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${schedule.availableSeats} ${viewModel.translate("avail_seats")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = BdGoldAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${schedule.rating} ★",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Right fare and Action
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${viewModel.translate("taka_unit")}${schedule.price}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(end = 12.dp)
                    )

                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = viewModel.translate("book_now"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Text(text = value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

// Global colors helpers
object Colors {
    val yellowTone = Color(0xFFD4AC0D)
    val blueTone = Color(0xFF2980B9)
}

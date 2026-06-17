package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

@Composable
fun ProfileTab(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val u by viewModel.currentUser.collectAsState()
    val activeBookings by viewModel.activeBookings.collectAsState()

    var customFundAmount by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 550.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar Icon Circle
                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = u.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${u.email}  |  ${u.phone}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Divider(modifier = Modifier.padding(vertical = 14.dp), color = Color.Gray.copy(alpha = 0.15f))

                // Wallet Balances Group
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = viewModel.translate("wallet_balance"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "${viewModel.translate("taka_unit")}${String.format("%.2f", u.walletBalance)}",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Log Out
                    IconButton(
                        onClick = { viewModel.logoutUser() },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), RoundedCornerShape(50))
                            .testTag("sign_out_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Add Funds Sandbox buttons
                Text(
                    text = viewModel.translate("add_funds") + " (Demo Sandbox):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(500.0, 1000.0, 5000.0).forEach { amt ->
                        Button(
                            onClick = {
                                viewModel.depositFunds(amt)
                                Toast.makeText(context, "Added ৳$amt Sandbox Cash!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Text(
                                text = "+${viewModel.translate("taka_unit")}${String.format("%.0f", amt)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language & Theme Custom Preferences Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 550.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Language Switches Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Translate, "Lang", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = viewModel.translate("language"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Switch(
                        checked = viewModel.isBangla,
                        onCheckedChange = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("lang_switch_toggle")
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.1f))

                // Dark Theme toggle row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, "Dark", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = viewModel.translate("theme"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Switch(
                        checked = viewModel.darkThemeOverride,
                        onCheckedChange = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("theme_switch_toggle")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // History of active booked tickets logs
        Text(
            text = viewModel.translate("active_bookings"),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 550.dp)
                .padding(start = 4.dp, bottom = 8.dp)
        )

        if (activeBookings.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 550.dp)
                    .height(90.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.translate("no_bookings"),
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            // Lazy load tickets log lists
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 550.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeBookings) { booking ->
                        ActiveTicketItemCard(tk = booking, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveTicketItemCard(tk: BkgTicket, viewModel: AppViewModel) {
    val transportIcon = when (tk.transportType) {
        TransportType.BUS -> Icons.Default.DirectionsBus
        TransportType.TRAIN -> Icons.Default.Train
        TransportType.LAUNCH -> Icons.Default.DirectionsBoat
        TransportType.FLIGHT -> Icons.Default.Flight
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = transportIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tk.companyName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(GreenSuccess.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tk.status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${tk.from} ➔ ${tk.to}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Seat: ${tk.seatNo}", fontSize = 11.sp, color = Color.Gray)
                Text(text = "Boarding PIN: ${tk.boardingCode}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

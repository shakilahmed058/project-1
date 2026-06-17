package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val u by viewModel.currentUser.collectAsState()
    val notificationList by viewModel.notifications.collectAsState()
    val unreadCount = notificationList.count { !it.read && it.title.isNotBlank() }

    var currentTabState by remember { mutableStateOf(0) } // 0=Search, 1=Rides/Rent, 2=Delivery/Track, 3=Admin, 4=Profile
    var isNotificationsOpen by remember { mutableStateOf(false) }

    // Edge checkout: If user is not authenticated, render LoginScreen
    if (!u.isLogged) {
        LoginScreen(viewModel = viewModel)
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (!isNotificationsOpen) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CholoBD",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.toggleLanguage() }) {
                            Icon(Icons.Default.Translate, contentDescription = "Language", tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    actions = {
                        // Digital Wallet display in toll bar
                        Card(
                            onClick = { currentTabState = 4 },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            ),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = viewModel.translate("taka_unit"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = String.format("%.0f", u.walletBalance),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Notification Bell Icon with real dynamic badge counts!
                        Box(
                            modifier = Modifier.padding(end = 8.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(onClick = { isNotificationsOpen = true }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notification inbox",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp, end = 4.dp)
                                        .size(16.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(Color.Red),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = unreadCount.toString(),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (!isNotificationsOpen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("app_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTabState == 0,
                        onClick = { currentTabState = 0 },
                        label = { Text(viewModel.translate("home"), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.DirectionsBus, contentDescription = null) },
                        modifier = Modifier.testTag("nav_search")
                    )

                    NavigationBarItem(
                        selected = currentTabState == 1,
                        onClick = { currentTabState = 1 },
                        label = { Text(viewModel.translate("rides_rent"), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
                        modifier = Modifier.testTag("nav_rides")
                    )

                    NavigationBarItem(
                        selected = currentTabState == 2,
                        onClick = { currentTabState = 2 },
                        label = { Text(viewModel.translate("delivery_track"), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.LocalShipping, contentDescription = null) },
                        modifier = Modifier.testTag("nav_parcel")
                    )

                    NavigationBarItem(
                        selected = currentTabState == 3,
                        onClick = { currentTabState = 3 },
                        label = { Text(viewModel.translate("admin"), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                        modifier = Modifier.testTag("nav_admin")
                    )

                    NavigationBarItem(
                        selected = currentTabState == 4,
                        onClick = { currentTabState = 4 },
                        label = { Text(viewModel.translate("profile"), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main viewport state navigation routing
            Crossfade(targetState = currentTabState) { state ->
                when (state) {
                    0 -> TransportSearchTab(viewModel = viewModel)
                    1 -> RideShareRentalTab(viewModel = viewModel)
                    2 -> DeliveryTrackingTab(viewModel = viewModel)
                    3 -> AdminDashboardTab(viewModel = viewModel)
                    4 -> ProfileTab(viewModel = viewModel)
                }
            }

            // Notification Screen Animated overlay
            AnimatedVisibility(
                visible = isNotificationsOpen,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            ) {
                NotificationScreen(
                    viewModel = viewModel,
                    onBack = { isNotificationsOpen = false }
                )
            }
        }
    }
}

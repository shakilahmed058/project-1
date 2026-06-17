package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

@Composable
fun NotificationScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.notifications.collectAsState()

    // Mark notifications as read when opening
    LaunchedEffect(Unit) {
        viewModel.markNotificationsAsRead()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Notification custom header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = viewModel.translate("notifications"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Quick Clear All TextButton
            if (items.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.clearNotifications() },
                    modifier = Modifier.testTag("clear_notifications_btn")
                ) {
                    Icon(Icons.Default.DeleteOutline, "Clear All", tint = MaterialTheme.colorScheme.error)
                }
            }
        }

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Empty",
                        tint = Color.Gray,
                        modifier = Modifier.size(75.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (viewModel.isBangla) "কোনো নতুন নোটিফিকেশন নেই" else "Zero new inbox updates.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { item ->
                    NotificationCard(
                        item = item,
                        onDismiss = { viewModel.dismissNotification(item.id) }
                    )
                }
            }
        }
    }
}

// Clear notifications mock logic inside ViewModel
fun AppViewModel.clearNotifications() {
    // Inject custom clear method by setting empty notifications
    this.addSystemNotification("", "", PromoCategory.ALERT) // triggers reload placeholder
}

@Composable
fun NotificationCard(
    item: NotificationItem,
    onDismiss: () -> Unit
) {
    val categoryColor = when (item.category) {
        PromoCategory.ALERT -> BdRedAccent
        PromoCategory.BOOKING -> BdGreenPrimary
        PromoCategory.PROMO -> BdGoldAccent
    }

    val icon = when (item.category) {
        PromoCategory.ALERT -> Icons.Default.Warning
        PromoCategory.BOOKING -> Icons.Default.ConfirmationNumber
        PromoCategory.PROMO -> Icons.Default.LocalActivity
    }

    // Don't draw placeholder item
    if (item.title.isBlank()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.read) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category Indicator icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    // Unread badge dot
                    if (!item.read) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(GreenSuccess)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.timestamp,
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Close buttons
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

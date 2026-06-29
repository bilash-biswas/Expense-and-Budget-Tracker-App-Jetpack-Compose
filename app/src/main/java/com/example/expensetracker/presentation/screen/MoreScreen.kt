package com.example.expensetracker.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensetracker.presentation.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navController: NavController
) {
    var showAboutDialog by remember { mutableStateOf(false) }

    val DataManagementOptions = listOf(
        SettingsOption(
            title = "Categories",
            description = "Manage expense category icons and colors",
            icon = Icons.Default.Category,
            color = Color(0xFF6366F1), // Indigo
            onClick = {
                navController.navigate("category_management")
            }
        ),
        SettingsOption(
            title = "Export Data",
            description = "Export transactions database directly",
            icon = Icons.Default.ImportExport,
            color = Color(0xFF10B981), // Emerald
            onClick = { navController.navigate("export") }
        ),
        SettingsOption(
            title = "Analytics Reports",
            description = "View detailed reports and spending trends",
            icon = Icons.Default.Analytics,
            color = Color(0xFFF59E0B), // Amber
            onClick = { navController.navigate(BottomNavItem.Analytics.route) }
        )
    )

    val AppSettingsOptions = listOf(
        SettingsOption(
            title = "Preferences & Style",
            description = "Customize theme modes and active limits",
            icon = Icons.Default.Settings,
            color = Color(0xFF3B82F6), // Blue
            onClick = { navController.navigate("settings") }
        ),
        SettingsOption(
            title = "Security & PIN Lock",
            description = "Manage local biometric and login settings",
            icon = Icons.Default.Shield,
            color = Color(0xFFEF4444), // Red
            onClick = { navController.navigate("settings") }
        )
    )

    val AboutOptions = listOf(
        SettingsOption(
            title = "About WalletFlow",
            description = "App version details and licenses",
            icon = Icons.Default.Info,
            color = Color(0xFF8B5CF6), // Purple
            onClick = { showAboutDialog = true }
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("More", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SettingsCategoryHeader(title = "Data Management")
            }
            items(DataManagementOptions) { option ->
                SettingsOptionItem(option = option)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsCategoryHeader(title = "App Configurations")
            }
            items(AppSettingsOptions) { option ->
                SettingsOptionItem(option = option)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsCategoryHeader(title = "System Info")
            }
            items(AboutOptions) { option ->
                SettingsOptionItem(option = option)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("WalletFlow", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Version 1.0.0",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("A premium and clean Jetpack Compose application designed to organize and budget your transactions, track limits dynamically, and secure local records.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Made with ♥ by Bilash Kumar Biswas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 4.dp),
        letterSpacing = 0.5.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsOptionItem(option: SettingsOption) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            ),
        onClick = option.onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(option.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = option.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

data class SettingsOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

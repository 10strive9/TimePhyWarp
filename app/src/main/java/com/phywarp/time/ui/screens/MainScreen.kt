package com.phywarp.time.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.phywarp.time.data.local.UserPreferences
import com.phywarp.time.ui.TimerViewModel

@Composable
fun MainScreen(viewModel: TimerViewModel, userPreferences: UserPreferences) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    var selectedTab by remember { mutableStateOf(0) }

    if (isLandscape) {
        // Tablet landscape: split screen
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1.5f)) {
                TimerScreen(viewModel, userPreferences)
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(400.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(8.dp)
            ) {
                LogScreen(viewModel)
            }
        }
    } else {
        // Portrait: Bottom navigation
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("计时") },
                        icon = { /* Icon */ }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("日志") },
                        icon = { /* Icon */ }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (selectedTab == 0) {
                    TimerScreen(viewModel, userPreferences)
                } else {
                    LogScreen(viewModel)
                }
            }
        }
    }
}

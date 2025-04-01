package dew.app.mobile.presentation.cockpit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import dew.app.mobile.presentation.chat.ChatScreen
import dew.app.mobile.presentation.chat.ChatViewModel
import dew.app.mobile.presentation.tasks.TasksScreen
import dew.app.mobile.presentation.tasks.TasksViewModel
import dew.app.mobile.presentation.today.TodayScreen
import dew.app.mobile.presentation.today.TodayViewModel

@Composable
fun CockpitScreen(){
    var selectedTabIndex by remember { mutableIntStateOf(1) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            contentColor = Color(0xFFFFFFFF),
            containerColor = MaterialTheme.colorScheme.primary,
            selectedTabIndex = selectedTabIndex,
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Today") },
                icon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Today") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("All Tasks") },
                icon = { Icon(imageVector = Icons.Default.Menu, contentDescription = "Today") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("Chat") },
                icon = { Icon(imageVector = Icons.Default.Star, contentDescription = "Today") }
            )
        }
        when (selectedTabIndex) {
            0 -> {
                val viewModel = hiltViewModel<TodayViewModel>()
                TodayScreen(viewModel)
            }
            1 -> {
                val viewModel = hiltViewModel<TasksViewModel>()
                TasksScreen(viewModel)
            }
            2 -> {
                val viewModel = hiltViewModel<ChatViewModel>()
                ChatScreen(viewModel)
            }
        }
    }

}
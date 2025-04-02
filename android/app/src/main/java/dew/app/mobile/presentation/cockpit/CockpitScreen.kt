package dew.app.mobile.presentation.cockpit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dew.app.mobile.presentation.chat.ChatScreen
import dew.app.mobile.presentation.chat.ChatViewModel
import dew.app.mobile.presentation.tasks.TasksScreen
import dew.app.mobile.presentation.tasks.TasksViewModel
import dew.app.mobile.presentation.today.TodayScreen
import dew.app.mobile.presentation.today.TodayViewModel

@Composable
fun CockpitScreen(){
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

        },
        bottomBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = Color.Gray,
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Today", style = MaterialTheme.typography.labelSmall) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Today",
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selectedTabIndex == 0) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                                .padding(5.dp)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("All Tasks",fontSize = 12.sp) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = "Today",
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selectedTabIndex == 1) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                                .padding(5.dp)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Chat",fontSize = 12.sp) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Today",
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selectedTabIndex == 2) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                                .padding(5.dp)
                        )
                    }
                )
            }
        },
        content = {
            Column(
                modifier = Modifier.padding(it)
            ) {
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
    )

}
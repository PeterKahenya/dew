package dew.app.mobile.presentation.today

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dew.app.mobile.TaskActivity
import dew.app.mobile.data.source.DbTask
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(viewModel: TodayViewModel) {
    val tasksState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // get current month, day and year
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val month = now.month.name.uppercase()
    val day = if(now.dayOfMonth < 10) "0${now.dayOfMonth}" else now.dayOfMonth.toString()
    val year = now.year.toString()
    val completed = tasksState.tasks.filter { it.isComplete }.size
    val pending = tasksState.tasks.filter { !it.isComplete }.size

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                context.startActivity(Intent(context, TaskActivity::class.java))
            }) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Add Task")
                    Text(text = "Add Task")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Column(modifier = Modifier.padding(it).padding(10.dp)) {
            Text(
                text = "Welcome back!".uppercase(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp)
            )
            Row (
                modifier = Modifier.fillMaxWidth().height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .height(100.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondary, // Start color
                                    MaterialTheme.colorScheme.secondary  // End color
                                )
                            )
                        )
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = month, style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSecondary))
                    Text(text = day, style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSecondary))
                    Text(text = year, style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSecondary))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxHeight()
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "$completed Tasks",style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimary))
                        Text(text = "Completed", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimary))
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "$pending Tasks",style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimary))
                        Text(text = "Pending", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimary))
                    }
                }
                }
            if (tasksState.error != null) {
                Text(text = tasksState.error!!)
            }
            Text(
                text="TASKS",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top=20.dp),
                color = MaterialTheme.colorScheme.inversePrimary
            )
            if (tasksState.tasks.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(top=20.dp).weight(1f),
                ) {
                    items(tasksState.tasks) { task ->
                        TaskItem(task, context)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: DbTask, context: Context) {
    Surface (
        modifier = Modifier.fillMaxWidth().background(Color.LightGray),
        onClick = {
            val intent = Intent(context, TaskActivity::class.java)
            intent.putExtra("taskId", task.id)
            context.startActivity(intent)
        }
    ) {
        Column {
            Column(
                modifier = Modifier.padding(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Edit Task",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.size(5.dp))
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

}
package dew.app.mobile.presentation.today

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dew.app.mobile.TaskActivity
import dew.app.mobile.data.source.DbTask


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(viewModel: TodayViewModel) {
    val tasksState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
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
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Column(modifier = Modifier.padding(it).padding(16.dp)) {
            if (tasksState.error != null) {
                Text(text = tasksState.error!!)
            }
            if (tasksState.tasks.isNotEmpty()) {
                LazyColumn {
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
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color.Gray
            )
        }
    }

}
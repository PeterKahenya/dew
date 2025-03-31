package dew.app.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dew.app.mobile.presentation.task.TaskScreen
import dew.app.mobile.presentation.task.TaskViewModel
import dew.app.mobile.presentation.ui.theme.DewTheme

@AndroidEntryPoint
class TaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskId = intent.getStringExtra("taskId")
        println("TaskActivity taskId: $taskId")
        setContent {
            DewTheme {
                val viewModel = hiltViewModel<TaskViewModel>()
                TaskScreen(viewModel, taskId)
            }
        }
    }
}
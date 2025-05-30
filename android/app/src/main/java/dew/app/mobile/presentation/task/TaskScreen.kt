package dew.app.mobile.presentation.task

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dew.app.mobile.CockpitActivity
import dew.app.mobile.data.model.TaskCreate
import dew.app.mobile.data.model.toTaskUpdate
import dew.app.mobile.data.source.DbTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    taskId: String? = null
){
    if (taskId != null){
        viewModel.loadTask(taskId)
    }
    val taskState by viewModel.state.collectAsStateWithLifecycle()
    val task: DbTask? = taskState.task
    val currentState = remember { mutableStateOf(TaskCreate("", "", false, null)) }

    LaunchedEffect(task) {
        task?.let {
            currentState.value = TaskCreate(
                title = it.title,
                description = it.description,
                isCompleted = it.isComplete,
                completedAt = it.completedAt
            )
        }
    }
    val context: Context = LocalContext.current
    if (taskState.isCreated){
        context.startActivity(Intent(context, CockpitActivity::class.java))
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 40.dp, horizontal = 16.dp).fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = currentState.value.title,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            onValueChange = {
                currentState.value = currentState.value.copy(title = it)
            },
            placeholder = { Text(text = "Title") },
            textStyle = MaterialTheme.typography.titleMedium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
            ),
        )
        OutlinedTextField(
            value = currentState.value.description,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(5.dp),
            onValueChange = {
                currentState.value = currentState.value.copy(description = it)
            },
            label = { Text(text = "Description", style = MaterialTheme.typography.bodySmall)},
            textStyle = MaterialTheme.typography.bodySmall,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
            ),
        )
        if (taskState.isUpdated){
            Text(
                text="updated",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Checkbox(
                    checked = currentState.value.isCompleted?:false,
                    onCheckedChange = {
                        currentState.value = currentState.value.copy(isCompleted = it)
                    }
                )
            }
            Button(
                onClick = {
                    if (taskId != null) {
                        viewModel.updateTask(taskId, currentState.value.toTaskUpdate())
                    } else {
                        viewModel.createTask(currentState.value)
                    }
                }
            ) {
                Text(text = "Save")
            }
        }

    }

}
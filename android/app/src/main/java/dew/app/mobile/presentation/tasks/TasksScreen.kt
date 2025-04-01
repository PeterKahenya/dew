package dew.app.mobile.presentation.tasks

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dew.app.mobile.TaskActivity
import dew.app.mobile.data.model.TaskFilters
import dew.app.mobile.data.model.toQueryMap
import dew.app.mobile.presentation.today.TaskItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: TasksViewModel) {
    val tasksState by viewModel.state.collectAsStateWithLifecycle()
    var viewFilterPanel by remember { mutableStateOf(false) }
    val filters: MutableState<TaskFilters> = remember {
        mutableStateOf(
            TaskFilters(
                createdAt = null,
                completedAt = null,
                status = "all",
                q = ""
            )
        )
    }

    val context = LocalContext.current
    Scaffold(floatingActionButton = {
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
    }, floatingActionButtonPosition = FabPosition.Center, topBar = {
        OutlinedTextField(
            value = filters.value.q,
            onValueChange = {
                filters.value = filters.value.copy(q = it)
                viewModel.getTasks(filters.value.toQueryMap())
                            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(Color(0xFFF5F5F5)),
            placeholder = { Text("Search") },
            leadingIcon = {
                IconButton(onClick = {
                    viewFilterPanel = !viewFilterPanel
                }) {
                    Icon(Icons.Default.Menu, contentDescription = "Search")
                }
            },
            trailingIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
            ),
        )
    }) {
        if (tasksState.error != null) {
            Text(text = tasksState.error!!)
        }
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            if (viewFilterPanel) {
                Box {
                    Column {
                        Row {
                            FilterChip(
                                onClick = {
                                    filters.value = filters.value.copy(status = "complete")
                                    viewModel.getTasks(filters.value.toQueryMap())
                                          },
                                label = { Text("Complete") },
                                selected = filters.value.status == "complete",
                                leadingIcon = if ( filters.value.status == "complete") {
                                    { Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {null}
                            )
                            FilterChip(
                                onClick = {
                                    filters.value = filters.value.copy(status = "pending")
                                    viewModel.getTasks(filters.value.toQueryMap())
                                          },
                                label = { Text("Pending") },
                                selected = filters.value.status == "pending",
                                leadingIcon = if ( filters.value.status == "pending") {
                                    { Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {null}
                            )
                            FilterChip(
                                onClick = {
                                    filters.value = filters.value.copy(status = "all")
                                    viewModel.getTasks(filters.value.toQueryMap())
                                          },
                                label = { Text("All") },
                                selected = filters.value.status == "all",
                                leadingIcon = if ( filters.value.status == "all") {
                                    { Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {null}
                            )
                        }
                    }
                }
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
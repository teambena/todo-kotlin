package com.example.todoapplication.repository

import androidx.lifecycle.LiveData
import com.example.todoapplication.data.Task
import com.example.todoapplication.data.TasksDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultTasksRepository(
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
) : TasksDataSource {

    override fun getAllTasks(): LiveData<List<Task>> = tasksLocalDataSource.getAllTasks()

    override fun getTaskByUid(uid: String): LiveData<List<Task>> {
        return tasksLocalDataSource.getTaskByUid(uid)
    }

    fun getTaskByUidRemote(uid: String): LiveData<List<Task>> = tasksRemoteDataSource.getTaskByUid(uid)

    override suspend fun insertTask(task: Task) {
        withContext(Dispatchers.IO) {
            coroutineScope {
                launch { tasksRemoteDataSource.insertTask(task) }
                launch { tasksLocalDataSource.insertTask(task) }
            }
        }
    }

    override suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            coroutineScope {
                launch { tasksRemoteDataSource.updateTask(task) }
                launch { tasksLocalDataSource.updateTask(task) }
            }
        }
    }

    override suspend fun deleteTask(task: Task) {
        withContext(Dispatchers.IO) {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteTask(task) }
                launch { tasksLocalDataSource.deleteTask(task) }
            }
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteAll() }
                launch { tasksLocalDataSource.deleteAll() }
            }
        }
    }

    override suspend fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        withContext(Dispatchers.IO) {
            coroutineScope {
                launch { tasksRemoteDataSource.onTaskCheckedChanged(task, isChecked) }
                launch { tasksLocalDataSource.onTaskCheckedChanged(task, isChecked) }
            }
        }
    }

}
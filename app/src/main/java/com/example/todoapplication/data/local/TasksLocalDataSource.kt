package com.example.todoapplication.data.local

import androidx.lifecycle.LiveData
import com.example.todoapplication.data.Task
import com.example.todoapplication.data.TasksDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksLocalDataSource (private val taskDao: TaskDao) : TasksDataSource{

    override fun getAllTasks(): LiveData<List<Task>> {
        return taskDao.getAllTasks()
    }

    override fun getTaskByUid(uid: String): LiveData<List<Task>> {
        return taskDao.getTaskByUid(uid)
    }

    override suspend fun insertTask(task: Task) {
        withContext(Dispatchers.IO){
            taskDao.insert(task)
        }
    }

    override suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO){
            taskDao.update(task)
        }
    }

    override suspend fun deleteTask(task: Task) {
        withContext(Dispatchers.IO){
            taskDao.delete(task)
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.IO){
            taskDao.deleteAll()
        }
    }

    override suspend fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        taskDao.update(task.copy(complete = isChecked))
    }

}
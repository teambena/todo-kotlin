package com.example.todoapplication.data

import androidx.lifecycle.LiveData

interface TasksDataSource {

    fun getAllTasks(): LiveData<List<Task>>

    fun getTaskByUid(uid: String): LiveData<List<Task>>

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun deleteAll()

    suspend fun onTaskCheckedChanged(task: Task, isChecked: Boolean)
}
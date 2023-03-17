package com.example.todoapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapplication.repository.DefaultTasksRepository
import com.example.todoapplication.data.Task
import com.example.todoapplication.data.local.TaskDatabase
import com.example.todoapplication.data.local.TasksLocalDataSource
import com.example.todoapplication.data.remote.TasksRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application){

    private val taskDao = TaskDatabase.getDatabase(application).taskDao()
    private val localDataSource: TasksLocalDataSource = TasksLocalDataSource(taskDao)
    private val remoteDataSource: TasksRemoteDataSource = TasksRemoteDataSource()
    private val defaultTasksRepository: DefaultTasksRepository = DefaultTasksRepository(tasksLocalDataSource = localDataSource, tasksRemoteDataSource = remoteDataSource)

    fun getTaskByUid(uid: String): LiveData<List<Task>> = defaultTasksRepository.getTaskByUid(uid)

    fun getTaskByUidRemote(uid: String): LiveData<List<Task>> = defaultTasksRepository.getTaskByUidRemote(uid)

    fun insert(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            defaultTasksRepository.insertTask(task)
        }
    }

    fun update(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            defaultTasksRepository.updateTask(task)
        }
    }

    fun delete(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            defaultTasksRepository.deleteTask(task)
        }
    }

    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            defaultTasksRepository.deleteAll()
        }
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            defaultTasksRepository.onTaskCheckedChanged(task, isChecked)
        }
    }

}
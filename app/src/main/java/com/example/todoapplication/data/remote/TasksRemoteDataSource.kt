package com.example.todoapplication.data.remote

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapplication.data.Task
import com.example.todoapplication.data.TasksDataSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

class TasksRemoteDataSource : TasksDataSource{

    private val db = Firebase.firestore
    private val collection = db.collection("task_table")
    var savedTask : MutableLiveData<List<Task>> = MutableLiveData()
    val settings = firestoreSettings {
        isPersistenceEnabled = true
    }

    init {
        db.firestoreSettings = settings
    }

    override fun getAllTasks(): LiveData<List<Task>> {
        TODO("No Implementation")

    }

    override fun getTaskByUid(uid: String): LiveData<List<Task>> {
        val user = Firebase.auth.currentUser
        collection
            .whereEqualTo("uid", user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                val tasks = mutableListOf<Task>()
                for (document in documents) {
                    Log.d(ContentValues.TAG, "UID: ${user?.uid}, ${document.id} => ${document.data}")
                    var taskItem = document.toObject(Task::class.java)
                    tasks.add(taskItem)
                }
                savedTask.value = tasks
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
        return savedTask
    }

    override suspend fun insertTask(task: Task) {
        collection
            .whereEqualTo("id", task.id)
            .get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.isEmpty) {
                    collection
                        .add(task)
                        .addOnSuccessListener { document ->
                            Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${document.id}")
                        }
                        .addOnFailureListener { error ->
                            Log.w(ContentValues.TAG, "Error adding document", error)
                        }
                } else {
                    Log.w(ContentValues.TAG, "id already existed")
                }
            }
    }

    override suspend fun updateTask(task: Task) {
        collection
            .whereEqualTo("id", task.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collection
                        .document(document.id)
                        .update("name", task.name,
                            "description", task.description)
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    override suspend fun deleteTask(task: Task) {
        collection
            .whereEqualTo("id", task.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    collection
                        .document(document.id)
                        .delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    override suspend fun deleteAll() {
        collection
            .whereEqualTo("complete", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    collection
                        .document(document.id)
                        .delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    override suspend fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        collection
            .whereEqualTo("id", task.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(isChecked){
                        collection
                            .document(document.id)
                            .update("complete", true)
                    }
                    else {
                        collection
                            .document(document.id)
                            .update("complete", false)
                    }
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}
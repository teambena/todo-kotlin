package com.example.todoapplication.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "task_table")
data class Task (

    @PrimaryKey
    var id: String,
    val name: String,
    val description: String,
    val created: Long = System.currentTimeMillis(),
    var uid: String,
    val complete: Boolean = false

): Parcelable {
    constructor() : this(
        "",
        "",
        "",
        System.currentTimeMillis(),
        "",
        false
    ) {
    }
}
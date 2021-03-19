package com.example.notsapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes_table")
class Note(@ColumnInfo(name = "Note_Text")val text: String) {
    @PrimaryKey(autoGenerate = true)var id =0
}
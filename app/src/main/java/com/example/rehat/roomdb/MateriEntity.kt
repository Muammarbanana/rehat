package com.example.rehat.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materi")
data class MateriEntity (
    @ColumnInfo(name = "isi")var isi: String,
    @PrimaryKey @ColumnInfo(name = "id") var id: String
)
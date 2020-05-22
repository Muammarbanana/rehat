package com.example.rehat.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materi")
data class MateriEntity (
    @ColumnInfo(name = "judul")var judul: String,
    @PrimaryKey @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "jenis") var jenis: String,
    @ColumnInfo(name = "gambar") var gambar: String,
    @ColumnInfo(name = "isi") var isi: String,
    @ColumnInfo(name = "color") var color: String,
    @ColumnInfo(name = "desc") var desc: String
)
package com.example.rehat.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.rehat.rvlistmateri.Materi

@Dao
interface MateriDAO {
    @Query("SELECT * from materi")
    fun getAll(): LiveData<List<MateriEntity>>

    @Insert(onConflict = REPLACE)
    fun insert(materi: MateriEntity)

    @Delete
    fun delete(materi: MateriEntity)

    @Query("SELECT * FROM materi WHERE id = :idmateri")
    fun getDatabyID(idmateri: String): MateriEntity

    @Query("DELETE FROM materi WHERE id = :idmateri AND iduser = :iduser")
    fun deleteDatabyID(idmateri: String, iduser: String)

    @Query("SELECT COUNT(*) FROM materi")
    fun getDataCount(): Int
}
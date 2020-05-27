package com.example.rehat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rehat.roomdb.MateriDAO
import com.example.rehat.roomdb.MateriEntity

class SharedViewModel(private val materiDao: MateriDAO): ViewModel() {
    val selected = MutableLiveData<String>()
    val datavoice = MutableLiveData<String>()
    private lateinit var materi: LiveData<List<MateriEntity>>

    init {
        subscribeMateri()
    }

    fun selectedTab(item: String) {
        selected.value = item
    }

    fun listenMateri(): LiveData<List<MateriEntity>> {
        return materi
    }

    private fun subscribeMateri() {
        materi = materiDao.getAll()
    }
}
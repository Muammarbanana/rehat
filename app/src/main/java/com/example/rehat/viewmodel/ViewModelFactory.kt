package com.example.rehat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rehat.roomdb.MateriDAO

class ViewModelFactory(val materiDao: MateriDAO) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MateriDAO::class.java).newInstance(materiDao)
    }
}
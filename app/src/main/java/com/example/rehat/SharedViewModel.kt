package com.example.rehat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    val selected = MutableLiveData<String>()

    fun selectedTab(item: String) {
        selected.value = item
    }
}
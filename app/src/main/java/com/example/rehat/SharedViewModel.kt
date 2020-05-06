package com.example.rehat

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    val selected = MutableLiveData<String>()

    fun selectedTab(item: String) {
        selected.value = item
    }
}
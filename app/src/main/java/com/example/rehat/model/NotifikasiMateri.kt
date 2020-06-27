package com.example.rehat.model

class NotifikasiMateri(val judul: String, val jenis: Int, val iduser: String, val message: String, val timestamp: Long, val statusbaca: Int)  {
    constructor(): this("", 0, "", "", -1, 0)
}
package com.example.rehat.model

class ChatMessage(val fromId: String, val toId: String, val message: String, val timestamp: Long) {
    constructor(): this("", "", "", -1)
}
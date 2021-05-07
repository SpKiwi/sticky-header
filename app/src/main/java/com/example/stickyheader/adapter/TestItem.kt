package com.example.stickyheader.adapter

data class TestItem(
    val id: Long,
    val position: Int,
    val coins: Long,
    val name: String,
    val isCurrentUser: Boolean
)
package com.example.stickyheader.adapter.model

data class TestItem(
    val id: Long,
    val position: Int,
    val coins: Long,
    val name: String,
    val isCurrentUser: Boolean
)
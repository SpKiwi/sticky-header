package com.example.stickyheader.adapter.model

import java.util.concurrent.atomic.AtomicLong

private val lastId: AtomicLong = AtomicLong(0)
private fun generateNextId(): Long = lastId.getAndIncrement()

fun generateInitialTestData(selfPosition: Int, listSize: Int): List<TestItem> {
    val result = mutableListOf<TestItem>()
    (0..listSize).forEach { position ->
        val newElement = if (selfPosition == position) {
            generateSelf(position)
        } else {
            generateStranger(position)
        }
        result.add(newElement)
    }
    return result
}

private fun generateSelf(position: Int) =
    TestItem(
        id = generateNextId(),
        position = position + 1,
        coins = 1000L - position,
        name = "Andrei Malaev",
        isCurrentUser = true
    )

private fun generateStranger(position: Int): TestItem =
    TestItem(
        id = generateNextId(),
        position = position + 1,
        coins = 1000L - position,
        name = "${names.random()} ${lastNames.random()}",
        isCurrentUser = false
    )

private val names: List<String> = listOf(
    "Albert",
    "Chara",
    "Ash",
    "Ashley",
    "Antony",
    "Andy",
    "Jamie",
    "Jack",
    "Bill",
    "Billie",
    "Ricky",
    "Michael",
    "Wesley"
)

private val lastNames: List<String> = listOf(
    "Dumbledore",
    "Martin",
    "Herrington",
    "Snape",
    "Potter",
    "Radcliff",
    "Jackson",
    "Johnson",
    "Drake",
    "Hoe",
    "Einstein",
    "Geller",
    "Tribiani",
    "Black",
    "Green",
    "Doe"
)
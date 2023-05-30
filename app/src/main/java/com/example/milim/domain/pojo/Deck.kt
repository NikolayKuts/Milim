package com.example.milim.domain.pojo

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

private const val DECKS_TABLE_NAME = "decks"

@Entity(tableName = DECKS_TABLE_NAME)
data class Deck(
    val id: Int,
    @PrimaryKey
    @NonNull
    val name: String,
    val size: Int = 0,
    val progress: Int = 0
) : Serializable {
    constructor(): this(-1, "empty")
}
package com.example.milim.domain.pojo

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "decks")
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
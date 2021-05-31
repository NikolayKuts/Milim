package com.example.milim.pojo

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val wordId: Int = 0,
    val deckId: Int,
    val word: String
)
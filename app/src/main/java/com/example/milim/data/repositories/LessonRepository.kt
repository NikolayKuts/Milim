package com.example.milim.data.repositories

import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word

interface LessonRepository {
    fun loadData(deckId: Int)
    fun reloadData(deckId: Int)
    fun updateWord(wordObject: Word)
    fun deleteWord(word: Word)
    fun updateDeck(deck: Deck)
    fun addWord(word: String, deckId: Int)
}
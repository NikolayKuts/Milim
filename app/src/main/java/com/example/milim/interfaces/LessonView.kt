package com.example.milim.interfaces

import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word

interface LessonView {
    fun setContent(wordsFromDB: List<Word>, deckFromDB: Deck)
    fun updateContent(wordsFromDB: List<Word>, deck: Deck)
    fun onUpdateDeck(deck: Deck)
}
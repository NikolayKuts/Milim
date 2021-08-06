package com.example.milim.presentation.screens.lesson

import android.content.Context
import com.example.milim.data.impementations.LessonFirebaseRepositoryImp
import com.example.milim.data.repositories.LessonRepository
import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word
import com.example.milim.presentation.interfaces.LessonView

class LessonPresenter(view: LessonView, context: Context) {
//    private val repository: LessonRepository = LessonRoomRepositoryImp(view, context)
    private val repository: LessonRepository = LessonFirebaseRepositoryImp(view)

    fun loadData(deckId: Int) {
        repository.loadData(deckId)
    }

    fun reloadData(deckId: Int) {
        repository.reloadData(deckId)
    }

    fun updateWord(wordObject: Word) {
        repository.updateWord(wordObject)
    }

    fun deleteWord(word: Word) {
        repository.deleteWord(word)
    }

    fun updateDeck(deck: Deck) {
        repository.updateDeck(deck)
    }

    fun addWord(word: String, deckId: Int) {
        repository.addWord(word, deckId)
    }
}
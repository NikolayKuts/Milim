package com.example.milim.presentation.screens.lesson

import android.content.Context
import com.example.milim.data.MilimDatabase
import com.example.milim.domain.Asynchronium
import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word
import com.example.milim.interfaces.LessonView

class LessonPresenter(private val view: LessonView, context: Context) {
    private val database = MilimDatabase.getInstance(context)


    fun loadData(deckId: Int) {
        val words = getWords(deckId)
        val deck = getDeckById(deckId)
        view.setContent(words, deck)
    }

    fun reloadData(deckId: Int) {
        view.updateContent(getWords(deckId), getDeckById(deckId))
    }

    private fun getDeckById(deckId: Int): Deck {
        val asynchronium = Asynchronium<Int, Deck>()
        asynchronium.execute(deckId) { database.decksDao().getDeckById(it) }
        val response = asynchronium.getResponse()
        response?.let { return it }
        return Deck(-1, "", 0, 0)
    }

    private fun getWords(deckId: Int): List<Word> {
        val asynchronium = Asynchronium<Int, List<Word>>()
        asynchronium.execute(deckId) { database.wordsDao().getWordsByIdDeck(it) }
        val response = asynchronium.getResponse()
        response?.let { return it }
        return listOf()
    }

    fun updateWord(wordObject: Word) {
        val asynchronium = Asynchronium<Word, Unit>()
        asynchronium.execute(wordObject) { database.wordsDao().insertWord(it) }
        updateDecks()
        view.updateContent(getWords(wordObject.deckId), getDeckById(wordObject.deckId))
    }

    fun deleteWord(word: Word) {
        deleteWord(word.wordId)
    }

    private fun deleteWord(wordId: Int) {
        val asynchronium = Asynchronium<Int, Unit>()
        asynchronium.execute(wordId) { database.wordsDao().deleteWordById(it) }
        updateDecks()
    }

    fun addWord(word: String, deckId: Int) {
        val asynchronium = Asynchronium<Unit, Int>()
        asynchronium.execute(Unit) { database.wordsDao().getMaxWordId() }
        val maxWordId = asynchronium.getResponse()
        maxWordId?.let {
            val wordIdForNewWord = maxWordId + 1
            val newWordObject = Word(wordIdForNewWord, deckId, word)
            Asynchronium<Word, Unit>().execute(newWordObject) { database.wordsDao().insertWord(it) }
        }
        updateDecks()
    }

    private fun updateDecks() {
        val asynchronium = Asynchronium<Unit, Unit>()
        asynchronium.execute(Unit) {
            val decks = database.decksDao().getAllDecks()
            for (deck in decks) {
                val quantityWords = database.wordsDao().getQuantityWordsInDeck(deck.id)
                val progress = when {
                    quantityWords == 0 -> 0
                    quantityWords == 1 && deck.progress == 0 -> 1
                    else -> deck.progress
                }
                val updatedDeck = Deck(deck.id, deck.name, quantityWords, progress)
                database.decksDao().addDeck(updatedDeck)
            }
        }
    }
    fun updateDeck(deck: Deck) {
        addDeck(deck)
        view.updateContent(getWords(deck.id), deck)
    }

    private fun addDeck(deck: Deck) {
        val asynchronium = Asynchronium<Deck, Unit>()
        asynchronium.execute(deck) { database.decksDao().addDeck(deck) }
    }
}
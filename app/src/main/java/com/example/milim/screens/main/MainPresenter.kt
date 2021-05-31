package com.example.milim.screens.main

import android.content.Context
import com.example.milim.data.Asynchronium
import com.example.milim.data.MilimDatabase
import com.example.milim.pojo.Deck
import com.example.milim.pojo.Word

class MainPresenter(private val context: Context) {
    private val database = MilimDatabase.getInstance(context)

    fun getDeckById(deckId: Int): Deck {
        val asynchronium = Asynchronium<Int, Deck>()
        asynchronium.execute(deckId) { database.decksDao().getDeckById(it)}
        val response = asynchronium.getResponse()
        response?.let { return it }
        return Deck(-1, "", 0, 0)
    }

    fun  addDeck(deck: Deck) {
        val asynchronium = Asynchronium<Deck, Unit>()
        asynchronium.execute(deck) { database.decksDao().addDeck(deck) }
    }

    fun updateDeck(deck: Deck) {
        addDeck(deck)
    }

    fun getAllDecks(): List<Deck> {
        val asynchronium = Asynchronium<Unit, List<Deck>>()
        asynchronium.execute(Unit) { database.decksDao().getAllDecks() }
        val response = asynchronium.getResponse()
        response?.let { return it }
        return listOf()
    }

    fun getMaxDeckId(): Int {
        val asynchronium = Asynchronium<Unit, Int>()
        asynchronium.execute(Unit) { database.decksDao().getMaxDeckId() }
        val response = asynchronium.getResponse()
        response?.let { return it }
        return -1
    }

    fun isDeckExist(name: String): Boolean {
        val asynchronium = Asynchronium<String, Boolean>()
        asynchronium.execute(name) { database.decksDao().isDeckExist(it) }
        val response = asynchronium.getResponse()
        return response?: true
        
    }

    private fun updateDecks() {
        val asynchronium = Asynchronium<Unit, Unit>()
        asynchronium.execute(Unit) {
            val decks = database.decksDao().getAllDecks()
            for (deck in decks) {
                val quantityWords = database.wordsDao().getQuantityWordsInDeck(deck.id)
                val progress = when (deck.progress) {
                    0 -> 1
                    else -> deck.progress
                }
                val updatedDeck = Deck(deck.id, deck.name, quantityWords, progress)
                database.decksDao().addDeck(updatedDeck)
            }
        }
    }

    fun deleteDeck(deck: Deck) {
        val asynchronium = Asynchronium<Deck, Unit>()
        asynchronium.execute(deck) { database.decksDao().deleteDeck(deck.id) }
    }

    fun getWords(deckId: Int): List<Word> {
        val asynchronium = Asynchronium<Int, List<Word>>()
        asynchronium.execute(deckId) { database.wordsDao().getWordsByIdDeck(it) }
        val response = asynchronium.getResponse()
        response?.let { return it }
        return listOf()
    }

    fun getWordById(wordId: Int): Word {
        val asynchronium = Asynchronium<Int, Word>()
        asynchronium.execute(wordId) { database.wordsDao().getWordById(it) }
        val word = asynchronium.getResponse()
        word?.let { return word }
        return Word(-1, -1, "empty word")
    }

    fun addWord(contentWord: String, deckId: Int) {
        val asynchronium = Asynchronium<Unit, Int>()
        asynchronium.execute(Unit) { database.wordsDao().getMaxWordId() }
        val maxWordId = asynchronium.getResponse()
        maxWordId?.let {
            val newWordObject = Word((maxWordId + 1), deckId, contentWord)
            Asynchronium<Word, Unit>().execute(newWordObject) { database.wordsDao().insertWord(it) }
        }
        updateDecks()
    }

    fun updateWord(wordObject: Word) {
        val asynchronium = Asynchronium<Word, Unit>()
        asynchronium.execute(wordObject) { database.wordsDao().insertWord(it) }
        updateDecks()
    }

    fun addWordList(words: List<Word>) {
        val asynchronium = Asynchronium<List<Word>, Unit>()
        asynchronium.execute(words) { database.wordsDao().insertWordList(it) }
        updateDecks()
    }

    fun deleteWord(word: Word) {
        val asynchronium = Asynchronium<Int, Unit>()
        asynchronium.execute(word.wordId) { database.wordsDao().deleteWordById(it) }
        updateDecks()
    }
}
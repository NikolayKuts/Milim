package com.example.milim.presentation.screens.main

import android.content.Context
import com.example.milim.domain.Asynchronium
import com.example.milim.data.MilimDatabase
import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word
import com.example.milim.interfaces.MainView

class MainPresenter(private val view: MainView, private val context: Context) {
    private val database = MilimDatabase.getInstance(context)
    var dialogHelper: DialogHelper? = null

    fun interface DialogHelper {
        fun dismissDialog()
    }



    private fun addDeck(deck: Deck) {
        val asynchronium = Asynchronium<Deck, Unit>()
        asynchronium.execute(deck) { database.decksDao().addDeck(deck) }
    }

    fun addDeck(deckName: String) {
        if (isDeckExist(deckName)) {
            view.showToastIfDeckExist()
        } else {
            addNewDeck(deckName)
            view.showToastOnDeckCreated()
            view.showData(getAllDecks())
            dialogHelper?.dismissDialog()
        }
    }

    fun addNewDeck(deckName: String) {
        val newDeckId = getMaxDeckId() + 1
        val newDeck = Deck(newDeckId, deckName)
        addDeck(newDeck)
        view.showData(getAllDecks())
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

    private fun getMaxDeckId(): Int {
        val asynchronium = Asynchronium<Unit, Int>()
        asynchronium.execute(Unit) { database.decksDao().getMaxDeckId() }
        val response = asynchronium.getResponse()
        response?.let { return it }
        return -1
    }

    private fun isDeckExist(name: String): Boolean {
        val asynchronium = Asynchronium<String, Boolean>()
        asynchronium.execute(name) { database.decksDao().isDeckExist(it) }
        val response = asynchronium.getResponse()
        return response ?: true

    }

    // is repeated
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

    fun deleteDeck(deck: Deck) {
        val asynchronium = Asynchronium<Deck, Unit>()
        asynchronium.execute(deck) { database.decksDao().deleteDeck(deck.id) }
        view.showData(getAllDecks())
    }


    fun getWordById(wordId: Int): Word {
        val asynchronium = Asynchronium<Int, Word>()
        asynchronium.execute(wordId) { database.wordsDao().getWordById(it) }
        val word = asynchronium.getResponse()
        word?.let { return word }
        return Word(-1, -1, "empty word")
    }

//    fun addWord(contentWord: String, deckId: Int) {
//        val asynchronium = Asynchronium<Unit, Int>()
//        asynchronium.execute(Unit) { database.wordsDao().getMaxWordId() }
//        val maxWordId = asynchronium.getResponse()
//        maxWordId?.let {
//            val newWordObject = Word((maxWordId + 1), deckId, contentWord)
//            Asynchronium<Word, Unit>().execute(newWordObject) { database.wordsDao().insertWord(it) }
//        }
//        updateDecks()
//    }


    fun addWordList(words: List<Word>) {
        val asynchronium = Asynchronium<List<Word>, Unit>()
        asynchronium.execute(words) { database.wordsDao().insertWordList(it) }
        updateDecks()
    }



    fun loadData() {
        view.showData(getAllDecks())
    }

    fun renameDeck(oldDeck: Deck, newDeck: Deck) {
        deleteDeck(oldDeck)
        addDeck(newDeck)
        view.showData(getAllDecks())
    }
}
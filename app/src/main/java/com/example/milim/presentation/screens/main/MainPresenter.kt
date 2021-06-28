package com.example.milim.presentation.screens.main

import android.content.Context
import com.example.milim.domain.Asynchronium
import com.example.milim.data.MilimDatabase
import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word
import com.example.milim.interfaces.MainView
import kotlinx.coroutines.*

class MainPresenter(private val view: MainView, private val context: Context) {
    private val database = MilimDatabase.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)
    var dialogHelper: DialogHelper? = null

    fun interface DialogHelper {
        fun dismissDialog()
    }

    fun onAddDeck(deckName: String) {
        scope.launch {
            if (isDeckExist(deckName)) {
                withContext(Dispatchers.Main) {
                    view.showToastIfDeckExist()
                }
            } else {
                addNewDeck(deckName)
                withContext(Dispatchers.Main) {
                    view.showToastOnDeckCreated()
                    view.showData(getAllDecks())
                    dialogHelper?.dismissDialog()
                }
            }
        }
    }

    fun deleteDeck(deck: Deck) {
        scope.launch {
            database.decksDao().deleteDeck(deck.id)
            withContext(Dispatchers.Main) {
                view.showData(getAllDecks())
            }
        }
    }

    fun loadData() {
        scope.launch {
            withContext(Dispatchers.Main) {
                view.showData(getAllDecks())
            }
        }
    }

    fun renameDeck(oldDeck: Deck, newDeck: Deck) {
        deleteDeck(oldDeck)
        scope.launch {
            insertDeck(newDeck)
            withContext(Dispatchers.Main) {
                view.showData(getAllDecks())
            }
        }
    }

    private suspend fun insertDeck(deck: Deck) {
        withContext(Dispatchers.IO) {
            database.decksDao().addDeck(deck)
        }
    }

    private suspend fun addNewDeck(deckName: String) {
        withContext(Dispatchers.IO) {
            val newDeckId = getMaxDeckId() + 1
            val newDeck = Deck(newDeckId, deckName)
            insertDeck(newDeck)
        }
    }

    private suspend fun getAllDecks(): List<Deck> {
        val result = mutableListOf<Deck>()
        withContext(Dispatchers.IO) {
            val decks = async {
                database.decksDao().getAllDecks()
            }
            result.addAll(decks.await())
        }
        return result
    }

    private suspend fun getMaxDeckId(): Int {
        var result = -1
        withContext(Dispatchers.IO) {
            val maxDeckId = async {
                database.decksDao().getMaxDeckId()
            }
            result = maxDeckId.await()
        }
        return result
    }

    private suspend fun isDeckExist(name: String): Boolean {
        var result = true
        withContext(Dispatchers.IO) {
            val isExist = async {
                database.decksDao().isDeckExist(name)
            }
            result = isExist.await()
        }
        return result
    }

    // is repeated
    private suspend fun updateDecks() {
        withContext(Dispatchers.IO) {
            val decks = getAllDecks()
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
}
package com.example.milim.data

import android.content.Context
import com.example.milim.domain.MainRepository
import com.example.milim.domain.pojo.Deck
import com.example.milim.interfaces.MainView
import kotlinx.coroutines.*

class MainRoomRepositoryImp(private val view: MainView, context: Context) : MainRepository {
    private val database = MilimDatabase.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onAddDeck(deckName: String, dismissDialog: () -> Unit) {
        scope.launch {
            if (isDeckExist(deckName)) {
                withContext(Dispatchers.Main) { view.showToastIfDeckExist() }
            } else {
                addNewDeck(deckName)
                withContext(Dispatchers.Main) {
                    view.showToastOnDeckCreated()
                    view.showData(getAllDecks())
                    dismissDialog()
                }
            }
        }
    }

    override fun deleteDeck(deck: Deck) {
        scope.launch {
            database.decksDao().deleteDeck(deck.id)
            withContext(Dispatchers.Main) { view.showData(getAllDecks()) }
        }
    }

    override fun loadData() {
        scope.launch {
            withContext(Dispatchers.Main) { view.showData(getAllDecks()) }
        }
    }

    override fun renameDeck(oldDeck: Deck, newDeck: Deck) {
        deleteDeck(oldDeck)
        scope.launch {
            insertDeck(newDeck)
            withContext(Dispatchers.Main) { view.showData(getAllDecks()) }
        }
    }

    private suspend fun addNewDeck(deckName: String) {
        withContext(Dispatchers.IO) {
            val newDeckId = getMaxDeckId() + 1
            val newDeck = Deck(newDeckId, deckName)
            insertDeck(newDeck)
        }
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

    private suspend fun insertDeck(deck: Deck) {
        withContext(Dispatchers.IO) { database.decksDao().addDeck(deck) }
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
}
package com.example.milim.data.impementations

import com.example.milim.data.databases.MilimFirebase
import com.example.milim.data.repositories.MainRepository
import com.example.milim.domain.pojo.Deck
import com.example.milim.presentation.interfaces.MainView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFirebaseRepositoryImp(private val view: MainView) : MainRepository {
    private val firebase = MilimFirebase()
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun loadData() {
        scope.launch(Dispatchers.Main){
            view.showData(firebase.getAllDecksAsync().await())
        }
    }

    override fun onAddDeck(deckName: String, dismissDialog: () -> Unit) {
        scope.launch {
            val isDeckExist = firebase.isDeckExistAsync(deckName).await()
            if (isDeckExist) {
                view.showToastIfDeckExist()
            } else {
                addNewDeck(deckName)
                withContext(Dispatchers.Main) {
                    view.showToastOnDeckCreated()
                    view.showData(firebase.getAllDecksAsync().await())
                    dismissDialog()
                }
            }
        }
    }

    override fun deleteDeck(deck: Deck) {
        scope.launch{
            firebase.deleteDeckAsync(deck.id).await()
            view.showData(firebase.getAllDecksAsync().await())
        }
    }

    override fun renameDeck(oldDeck: Deck, newDeck: Deck) {
        scope.launch {
            firebase.deleteDeckAsync(oldDeck.id).await()
            scope.launch(Dispatchers.Main) {
                firebase.addDeckAsync(newDeck).await()
//            insertDeck(newDeck)
                view.showData(firebase.getAllDecksAsync().await())
            }
        }
    }

    private suspend fun addNewDeck(deckName: String) {
        withContext(Dispatchers.IO) {
            val newDeckId = firebase.getMaxDeckIdAsync().await() + 1
            val newDeck = Deck(newDeckId, deckName)
            firebase.addDeckAsync(newDeck).await()
        }
    }
}
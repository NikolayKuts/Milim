package com.example.milim.presentation.screens.main

import android.content.Context
import com.example.milim.data.impementations.MainFirebaseRepositoryImp
import com.example.milim.data.repositories.MainRepository
import com.example.milim.domain.pojo.Deck
import com.example.milim.presentation.interfaces.MainView

class MainPresenter(view: MainView, context: Context) {
//    private val repository: MainRepository = MainRoomRepositoryImp(view, context)
    private val repository: MainRepository = MainFirebaseRepositoryImp(view)


    fun loadData() {
        repository.loadData()
    }

    fun onAddDeck(deckName: String, dismissDialog: () -> Unit) {
        repository.onAddDeck(deckName, dismissDialog)
    }

    fun deleteDeck(deck: Deck) {
        repository.deleteDeck(deck)
    }

    fun renameDeck(oldDeck: Deck, newDeck: Deck) {
        repository.renameDeck(oldDeck, newDeck)
    }
}
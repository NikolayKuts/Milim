package com.example.milim.presentation.screens.main

import android.content.Context
import com.example.milim.data.MainRoomRepositoryImp
import com.example.milim.domain.MainRepository
import com.example.milim.domain.pojo.Deck
import com.example.milim.interfaces.MainView

class MainPresenter(view: MainView, context: Context) {
    private val repository: MainRepository = MainRoomRepositoryImp(view, context)

    fun onAddDeck(deckName: String, dismissDialog: () -> Unit) {
        repository.onAddDeck(deckName, dismissDialog)
    }

    fun deleteDeck(deck: Deck) {
        repository.deleteDeck(deck)
    }

    fun loadData() {
        repository.loadData()
    }

    fun renameDeck(oldDeck: Deck, newDeck: Deck) {
        repository.renameDeck(oldDeck, newDeck)
    }
}
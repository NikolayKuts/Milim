package com.example.milim.domain

import com.example.milim.domain.pojo.Deck

interface MainRepository {
    fun onAddDeck(deckName: String, dismissDialog: () -> Unit)
    fun deleteDeck(deck: Deck)
    fun loadData()
    fun renameDeck(oldDeck: Deck, newDeck: Deck)

}
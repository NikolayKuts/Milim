package com.example.milim.screens.main

import com.example.milim.pojo.Deck

interface IMainActivityView {
    fun getAllDecks()
    fun addDeck(deck: Deck)
}
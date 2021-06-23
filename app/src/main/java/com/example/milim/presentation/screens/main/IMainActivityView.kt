package com.example.milim.presentation.screens.main

import com.example.milim.domain.pojo.Deck

interface IMainActivityView {
    fun getAllDecks()
    fun addDeck(deck: Deck)
}
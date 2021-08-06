package com.example.milim.data.repositories

import com.example.milim.presentation.interfaces.WordBrowserView

interface WordBrowserRepository {
    fun loadData(view: WordBrowserView, deckId: Int)
}
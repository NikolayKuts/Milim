package com.example.milim.domain

import com.example.milim.interfaces.WordBrowserView

interface WordBrowserRepository {
    fun loadData(view: WordBrowserView, deckId: Int)
}
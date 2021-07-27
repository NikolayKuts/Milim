package com.example.milim.presentation.screens.word_browser

import android.content.Context
import com.example.milim.domain.WordBrowserRepository
import com.example.milim.data.WordBrowserRoomRepositoryImp
import com.example.milim.interfaces.WordBrowserView

class WordBrowserPresenter(private val view: WordBrowserView, context: Context) {
    private val repository: WordBrowserRepository = WordBrowserRoomRepositoryImp(context)

    fun loadData(deckId: Int) {
        repository.loadData(view, deckId)
    }
}
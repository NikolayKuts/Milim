package com.example.milim.presentation.screens.word_browser

import android.content.Context
import com.example.milim.domain.WordBrowserRepository
import com.example.milim.data.WordBrowserRoomRepositoryImp
import com.example.milim.data.dao.WordBrowserFirebaseRepositoryImp
import com.example.milim.interfaces.WordBrowserView

class WordBrowserPresenter(private val view: WordBrowserView, context: Context) {
    private val repository: WordBrowserRepository = WordBrowserFirebaseRepositoryImp()

    fun loadData(deckId: Int) {
        repository.loadData(view, deckId)
    }
}
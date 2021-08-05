package com.example.milim.data.dao

import com.example.milim.data.MilimFirebase
import com.example.milim.domain.WordBrowserRepository
import com.example.milim.interfaces.WordBrowserView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordBrowserFirebaseRepositoryImp() : WordBrowserRepository {
    private val database = MilimFirebase()
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun loadData(view: WordBrowserView, deckId: Int) {
        scope.launch {
            val words = database.getWordsByDeckId(deckId)
            view.showData(words)
        }
    }
}
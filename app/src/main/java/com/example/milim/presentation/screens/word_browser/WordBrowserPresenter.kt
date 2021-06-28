package com.example.milim.presentation.screens.word_browser

import android.content.Context
import com.example.milim.data.MilimDatabase
import com.example.milim.domain.Asynchronium
import com.example.milim.domain.pojo.Word
import com.example.milim.interfaces.WordBrowserView
import kotlinx.coroutines.*

class WordBrowserPresenter(private val view: WordBrowserView, private val context: Context) {
    private val database = MilimDatabase.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    fun loadData(deckId: Int) {
        scope.launch(Dispatchers.Main) {
            view.showData(getWords(deckId))
        }
    }

    private  suspend fun getWords(deckId: Int): List<Word> {
        val result = mutableListOf<Word>()
        withContext(Dispatchers.IO) {
            val words = async {
                database.wordsDao().getWordsByDeckId(deckId)
            }
            result.addAll(words.await())
        }
        return result
    }
}
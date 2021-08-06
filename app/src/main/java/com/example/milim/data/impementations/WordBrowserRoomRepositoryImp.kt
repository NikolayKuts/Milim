package com.example.milim.data.impementations

import android.content.Context
import com.example.milim.data.databases.MilimDatabase
import com.example.milim.data.repositories.WordBrowserRepository
import com.example.milim.domain.pojo.Word
import com.example.milim.presentation.interfaces.WordBrowserView
import kotlinx.coroutines.*

class WordBrowserRoomRepositoryImp(context: Context) : WordBrowserRepository {
    private val database = MilimDatabase.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun loadData(view: WordBrowserView, deckId: Int) {
        scope.launch(Dispatchers.IO) {
            view.showData(getWords(deckId))
        }
    }

    private suspend fun getWords(deckId: Int): List<Word> {
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
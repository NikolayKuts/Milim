package com.example.milim.presentation.screens.word_browser

import android.content.Context
import com.example.milim.data.MilimDatabase
import com.example.milim.domain.Asynchronium
import com.example.milim.domain.pojo.Word
import com.example.milim.interfaces.WordBrowserView

class WordBrowserPresenter(private val view: WordBrowserView, private val context: Context) {
    private val database = MilimDatabase.getInstance(context)

    fun loadData(deckId: Int) {
        view.showData(getWords(deckId))
    }

    private fun getWords(deckId: Int): List<Word> {
        val asynchronium = Asynchronium<Int, List<Word>>()
        asynchronium.execute(deckId) { database.wordsDao().getWordsByIdDeck(it) }
        val response = asynchronium.getResponse()
        response?.let { return it }
        return listOf()
    }

}
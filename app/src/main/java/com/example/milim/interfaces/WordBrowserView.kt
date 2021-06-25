package com.example.milim.interfaces

import com.example.milim.domain.pojo.Word

interface WordBrowserView {
    fun showData(wordsFromDB: List<Word>)
}
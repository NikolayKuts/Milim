package com.example.milim.data.dao

import com.example.milim.domain.pojo.Word
import kotlinx.coroutines.Deferred

interface FirebaseWordsDao{
    suspend fun getWordsByDeckId(deckId: Int): List<Word>
    suspend fun getWordByIdAsync(wordId: Int): Deferred<Word>
    suspend fun insertWordAsync(word: Word): Deferred<Unit>
    suspend fun getQuantityWordsInDeckAsync(deckId: Int): Deferred<Int>
    suspend fun getMaxWordIdAsync(): Deferred<Int>
    suspend fun deleteWordByIdAsync(wordId: Int): Deferred<Unit>
}
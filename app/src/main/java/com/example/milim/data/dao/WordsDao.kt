package com.example.milim.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.milim.domain.pojo.Word

@Dao
interface WordsDao {
    @Query("SELECT * FROM words WHERE deckId = :deckId")
    fun getWordsByDeckId(deckId: Int): List<Word>

    @Query("SELECT * FROM words WHERE wordId = :wordId")
    fun getWordById(wordId: Int): Word

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWordList(words: List<Word>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: Word)

    @Query("SELECT COUNT(*) FROM words WHERE deckId = :deckId")
    fun getQuantityWordsInDeck(deckId: Int): Int

    @Query("DELETE FROM words WHERE wordId = :wordId")
    fun deleteWordById(wordId: Int)

    @Query("SELECT MAX(wordId) FROM words")
    fun getMaxWordId(): Int
}
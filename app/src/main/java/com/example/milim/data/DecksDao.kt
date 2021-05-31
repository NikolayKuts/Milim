package com.example.milim.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.milim.pojo.Deck

@Dao
interface DecksDao {
    @Query("SELECT * FROM decks")
    fun getAllDecks(): List<Deck>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDeck(deck: Deck)

    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getDeckById(deckId: Int): Deck

    @Query("SELECT * FROM decks WHERE name = :name")
    fun isDeckExist(name: String): Boolean

    @Query("SELECT MAX(id) FROM decks")
    fun getMaxDeckId(): Int

    @Query("DELETE FROM decks WHERE id = :deckId")
    fun deleteDeck(deckId: Int)
}
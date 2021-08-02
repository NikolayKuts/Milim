package com.example.milim.data.dao

import com.example.milim.domain.pojo.Deck
import kotlinx.coroutines.Deferred

interface FirebaseDecksDao {
    suspend fun getAllDecksAsync(): Deferred<List<Deck>>
    suspend fun addDeckAsync(deck: Deck): Deferred<Unit>
    suspend fun getDeckById(deckId: Int): Deck
    suspend fun isDeckExistAsync(name: String): Deferred<Boolean>
    suspend fun getMaxDeckIdAsync(): Deferred<Int>
    suspend fun deleteDeckAsync(deckId: Int): Deferred<Unit>
    suspend fun updateDeck(deckId: Int, increaseSizeBy: Int)
}
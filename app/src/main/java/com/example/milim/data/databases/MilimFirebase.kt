package com.example.milim.data.databases

import android.util.Log
import com.example.milim.data.dao.FirebaseDecksDao
import com.example.milim.data.dao.FirebaseWordsDao
import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val WORDS_COLLECTION = "words_collection"
private const val DECKS_COLLECTION = "decks_collection"

class MilimFirebase : FirebaseDecksDao, FirebaseWordsDao {
    private val database = Firebase.firestore

    override suspend fun getWordsByDeckId(deckId: Int): List<Word> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<List<Word>>()
            database.collection(WORDS_COLLECTION).get().addOnSuccessListener { snapShot ->
                val words = snapShot.mapNotNull { docSnapshot -> docSnapshot.toObject<Word>() }
                    .filter { word -> word.deckId == deckId }
                deferred.complete(words)
            }
            deferred.await()
        }
    }

    override suspend fun getWordByIdAsync(wordId: Int): Deferred<Word> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Word>()
            database.collection(WORDS_COLLECTION)
                .get()
                .addOnSuccessListener { snapShot ->
                    val words = snapShot.mapNotNull { docSnapshot -> docSnapshot.toObject<Word>() }
                        .filter { word -> word.wordId == wordId }
                    deferred.complete(words[0])
                }
            deferred
        }
    }

    override suspend fun insertWordAsync(word: Word): Deferred<Unit> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Unit>()
            database.collection(WORDS_COLLECTION)
                .document(word.wordId.toString())
                .set(word).addOnSuccessListener { deferred.complete(Unit) }
            deferred
        }
    }

    override suspend fun getQuantityWordsInDeckAsync(deckId: Int): Deferred<Int> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Int>()
            database.collection(WORDS_COLLECTION)
                .get()
                .addOnSuccessListener { snapshot ->
                    val wordQuantity = snapshot.map { docSnapshot -> docSnapshot.toObject<Word>() }
                        .filter { word -> word.deckId == deckId }.size
                    deferred.complete(wordQuantity)
                }
            deferred
        }
    }

    override suspend fun getMaxWordIdAsync(): Deferred<Int> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Int>()
            val words = getAllWordsAsync().await()
            val maxId = words.map { word -> word.wordId }.maxOrNull() ?: 0
            deferred.complete(maxId)
            deferred
        }
    }

    override suspend fun deleteWordByIdAsync(wordId: Int): Deferred<Unit> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Unit>()
            database.collection(WORDS_COLLECTION)
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.forEach { docSnapshot ->
                        val word = docSnapshot.toObject<Word>()
                        if (word.wordId == wordId) {
                            docSnapshot.reference.delete().addOnSuccessListener {
                                deferred.complete(Unit)
                            }
                        }
                    }
                }
            deferred
        }
    }

    private suspend fun getAllWordsAsync(): Deferred<List<Word>> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<List<Word>>()
            database.collection(WORDS_COLLECTION).get().addOnSuccessListener { snapshot ->
                val words = snapshot.mapNotNull { docSnapshot -> docSnapshot.toObject<Word>() }
                deferred.complete(words)
            }
            deferred
        }
    }

    override suspend fun getAllDecksAsync(): Deferred<List<Deck>> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<List<Deck>>()
            database.collection(DECKS_COLLECTION)
                .get()
                .addOnSuccessListener { snapshot ->
                    val decks = snapshot.map { docSnapshot -> docSnapshot.toObject<Deck>() }
                    deferred.complete(decks)
                }
            deferred
        }
    }

    override suspend fun getDeckById(deckId: Int): Deck {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Deck>()
            database.collection(DECKS_COLLECTION)
                .get()
                .addOnSuccessListener { snapshot ->
                    val deck = snapshot.mapNotNull { docSnapshot -> docSnapshot.toObject<Deck>() }
                        .single { deck -> deck.id == deckId }
                    deferred.complete(deck)
                }
            deferred.await()
        }
    }

    override suspend fun addDeckAsync(deck: Deck): Deferred<Unit> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Unit>()
            database.collection(DECKS_COLLECTION)
                .document(deck.id.toString())
                .set(deck)
                .addOnSuccessListener { deferred.complete(Unit) }
            deferred
        }
    }

    override suspend fun isDeckExistAsync(name: String): Deferred<Boolean> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Boolean>()
            val decks = getAllDecksAsync().await()
            val isExist = decks.map { deck -> deck.name }.contains(name)
            deferred.complete(isExist)
            deferred
        }
    }

    override suspend fun getMaxDeckIdAsync(): Deferred<Int> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Int>()
            val decks = getAllDecksAsync().await()
            val maxId = decks.map { deck -> deck.id }.maxOrNull() ?: 0
            deferred.complete(maxId)
            deferred
        }
    }

    override suspend fun deleteDeckAsync(deckId: Int): Deferred<Unit> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Unit>()
            database.collection(DECKS_COLLECTION).get().addOnSuccessListener { snapshot ->
                snapshot.forEach { docSnapshot ->
                    val deck = docSnapshot.toObject<Deck>()
                    if (deck.id == deckId) {
                        docSnapshot.reference.delete().addOnSuccessListener {
                            deferred.complete(Unit)
                        }
                    }
                }
            }
            deferred
        }
    }

    override suspend fun updateDeck(deckId: Int, increaseSizeBy: Int) {
        withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Unit>()
            val oldDeck = getDeckById(deckId)
            val newSize = oldDeck.size + increaseSizeBy
            val progress = when {
                newSize == 0 -> 0
                newSize == 1 && oldDeck.progress == 0 -> 1
                else -> oldDeck.progress
            }
            val updatedDeck = Deck(deckId, oldDeck.name, newSize, progress)
            database.collection(DECKS_COLLECTION)
                .document(deckId.toString())
                .set(updatedDeck)
                .addOnSuccessListener { deferred.complete(Unit) }
        }
    }
}
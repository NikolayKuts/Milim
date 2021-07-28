package com.example.milim.data

import android.content.Context
import com.example.milim.domain.LessonRepository
import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word
import com.example.milim.interfaces.LessonView
import kotlinx.coroutines.*

class LessonRoomRepositoryImp(private val view: LessonView, context: Context) : LessonRepository {
    private val database = MilimDatabase.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun loadData(deckId: Int) {
        scope.launch(Dispatchers.Main) {
            val words = getWords(deckId)
            val deck = getDeckById(deckId)
            view.setContent(words, deck)
        }
    }

    override fun reloadData(deckId: Int) {
        scope.launch(Dispatchers.Main) {
            view.updateContent(getWords(deckId), getDeckById(deckId))
        }
    }

    override fun updateWord(wordObject: Word) {
        scope.launch {
            database.wordsDao().insertWord(wordObject)
            updateDecks()
            withContext(Dispatchers.Main) {
                val deckId = wordObject.deckId
                view.updateContent(getWords(deckId), getDeckById(deckId))
            }
        }
    }

    override fun deleteWord(word: Word) {
        scope.launch {
            database.wordsDao().deleteWordById(word.wordId)
            updateDecks()
        }
    }

    override fun updateDeck(deck: Deck) {
        scope.launch {
            addDeck(deck)
            withContext(Dispatchers.Main) {
                view.updateContent(getWords(deck.id), deck)
            }
        }
    }

    override fun addWord(word: String, deckId: Int) {
        scope.launch {
            val maxWordId = database.wordsDao().getMaxWordId()
            val newWordId = maxWordId + 1
            val newWordObject = Word(newWordId, deckId, word)
            database.wordsDao().insertWord(newWordObject)
            updateDecks()
        }
    }

    private suspend fun addDeck(deck: Deck) {
        withContext(Dispatchers.IO) {
            database.decksDao().addDeck(deck)
        }
    }

    private suspend fun getDeckById(deckId: Int): Deck {
        var result = Deck(-1, "no name")
        withContext(Dispatchers.IO) {
            val deck = async { database.decksDao().getDeckById(deckId) }
            result = deck.await()
        }
        return result
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

    private suspend fun updateDecks() {
        withContext(Dispatchers.IO) {
            val decks = database.decksDao().getAllDecks()
            for (deck in decks) {
                val quantityWords = database.wordsDao().getQuantityWordsInDeck(deck.id)
                val progress = when {
                    quantityWords == 0 -> 0
                    quantityWords == 1 && deck.progress == 0 -> 1
                    else -> deck.progress
                }
                val updatedDeck = Deck(deck.id, deck.name, quantityWords, progress)
                database.decksDao().addDeck(updatedDeck)
            }
        }
    }
}
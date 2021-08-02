package com.example.milim.data

import com.example.milim.domain.LessonRepository
import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word
import com.example.milim.interfaces.LessonView
import kotlinx.coroutines.*

class LessonFirebaseRepositoryImp(private val view: LessonView) : LessonRepository {
    private val firebase = MilimFirebase()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun loadData(deckId: Int) {
        scope.launch(Dispatchers.Main) {
            val words = async { firebase.getWordsByDeckId(deckId) }
            val deck = async { firebase.getDeckById(deckId) }
            view.setContent(words.await(), deck.await())
        }
    }

    override fun reloadData(deckId: Int) {
        scope.launch { updateViewContent(deckId) }
    }

    override fun updateWord(wordObject: Word) {
        scope.launch(Dispatchers.Main) {
            firebase.deleteWordByIdAsync(wordObject.wordId).await()
            firebase.insertWordAsync(wordObject).await()
            updateViewContent(wordObject.deckId)
        }
    }

    override fun deleteWord(word: Word) {
        scope.launch {
            firebase.deleteWordByIdAsync(word.wordId).await()
            updateViewContent(word.deckId)
        }
    }

    override fun updateDeck(deck: Deck) {
        scope.launch(Dispatchers.Main) {
            val deferred = firebase.addDeckAsync(deck)
            deferred.await()
            view.onUpdateDeck(deck)
        }
    }

    override fun addWord(word: String, deckId: Int) {
        scope.launch(Dispatchers.Main) {
            val maxWordId = firebase.getMaxWordIdAsync().await()
            val newWordId = maxWordId + 1
            val newWordObject = Word(newWordId, deckId, word)
            firebase.insertWordAsync(newWordObject).await()
            firebase.updateDeck(deckId, increaseSizeBy = 1)
            updateViewContent(deckId)
        }
    }

    private suspend fun updateViewContent(deckId: Int) {
        withContext(Dispatchers.Main) {
            val words = async { firebase.getWordsByDeckId(deckId) }
            val deck = async { firebase.getDeckById(deckId) }
            view.updateContent(words.await(), deck.await())
        }
    }



}
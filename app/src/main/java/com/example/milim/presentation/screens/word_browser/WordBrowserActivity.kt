package com.example.milim.presentation.screens.word_browser

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.milim.presentation.adapters.WordBrowserAdapter
import com.example.milim.databinding.ActivityWordBrowserBinding
import com.example.milim.domain.pojo.Word
import com.example.milim.presentation.interfaces.WordBrowserView

class WordBrowserActivity : AppCompatActivity(), WordBrowserView {
    private lateinit var presenter: WordBrowserPresenter
    private lateinit var browsedWords: MutableList<Word>
    private var adapter: WordBrowserAdapter? = null
    private var deckId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWordBrowserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        presenter = WordBrowserPresenter(this, applicationContext)
        browsedWords = mutableListOf()
        deckId = intent.getIntExtra(TAG_DECK_ID, -1)

        binding.recyclerViewWordBrowser.layoutManager = LinearLayoutManager(applicationContext)
        adapter = WordBrowserAdapter(applicationContext)
        binding.recyclerViewWordBrowser.adapter = adapter
        adapter?.words = browsedWords

        presenter.loadData(deckId)
    }

    override fun showData(wordsFromDB: List<Word>) {
        browsedWords.clear()
        browsedWords.addAll(wordsFromDB)
        adapter?.words = browsedWords
    }

    companion object {
        private const val TAG_DECK_ID = "deck_id"
        fun newIntent(context: Context, deckId: Int): Intent {
            return Intent(context, WordBrowserActivity::class.java).apply {
                putExtra(TAG_DECK_ID, deckId)
            }
        }
    }

}
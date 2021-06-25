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
import com.example.milim.interfaces.WordBrowserView
import com.example.milim.presentation.screens.main.MainPresenter

class WordBrowserActivity : AppCompatActivity(), WordBrowserView {
    private lateinit var presenter: WordBrowserPresenter
    private lateinit var words: MutableList<Word>
    private var deckId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWordBrowserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        presenter = WordBrowserPresenter(this, applicationContext)
        //val presenter = MainPresenter(applicationContext)
        words = mutableListOf()
        deckId = intent.getIntExtra(TAG_DECK_ID, -1)

        binding.recyclerViewWordBrowser.layoutManager = LinearLayoutManager(applicationContext)
        val adapter = WordBrowserAdapter(applicationContext)
        binding.recyclerViewWordBrowser.adapter = adapter
        adapter.words = words

        presenter.loadData(deckId)
    }

    override fun showData(wordsFromDB: List<Word>) {
        words.clear()
        words.addAll(wordsFromDB)
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
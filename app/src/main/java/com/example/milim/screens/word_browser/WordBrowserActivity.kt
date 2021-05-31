package com.example.milim.screens.word_browser

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.milim.R
import com.example.milim.adapters.WordBrowserAdapter
import com.example.milim.databinding.ActivityWordBrowserBinding
import com.example.milim.screens.main.MainPresenter

class WordBrowserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWordBrowserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val presenter = MainPresenter(applicationContext)
        val wordsFromDB = presenter.getWords(intent.getIntExtra(TAG_DECK_ID, -1))
        for (word in wordsFromDB) {
            Log.i("word_browser", "onCreate: $word") }

        binding.recyclerViewWordBrowser.layoutManager = LinearLayoutManager(applicationContext)
        val adapter = WordBrowserAdapter(applicationContext)
        binding.recyclerViewWordBrowser.adapter = adapter
        adapter.words = wordsFromDB
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
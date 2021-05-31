package com.example.milim.screens.adding_word

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.milim.R
import com.example.milim.databinding.ActivityAddWordBinding
import com.example.milim.pojo.Word
import com.example.milim.screens.main.MainPresenter

class AddWordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddWordBinding
    private var deckId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        deckId = intent.getIntExtra(TAG_DECK_ID, -1)
    }

    companion object {
        private const val TAG_DECK_ID = "deck_id"
        fun newIntent(context: Context, deckId: Int): Intent {
            return Intent(context, AddWordActivity::class.java).apply {
                putExtra(TAG_DECK_ID, deckId)
            }
        }
    }

    fun onInsertWordButtonClick(view: View) {
        val newWord = binding.editTextNewWord.text.toString()
        if (newWord != "") {
            insetWord(view.context, newWord)
            binding.editTextNewWord.setText("")
            Toast.makeText(view.context, "The word has been added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(view.context, "Type a word", Toast.LENGTH_SHORT).show()
        }
    }

    fun onCancelWordAddingClick(view: View) {}

    private fun insetWord(context: Context, newWord: String) {
        val presenter = MainPresenter(context)
        presenter.addWord(newWord, deckId)
    }
}
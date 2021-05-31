package com.example.milim.screens.edition_word

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.milim.databinding.ActivityEditWordBinding
import com.example.milim.pojo.Word
import com.example.milim.screens.main.MainPresenter

class EditWordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditWordBinding
    private lateinit var wordObject: Word
    private lateinit var wordForEdition: String
    private var wordId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditWordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        wordId = intent.getIntExtra(TAG_WORD_ID, -1)
        val presenter = MainPresenter(applicationContext)
        wordObject = presenter.getWordById(wordId)
        wordForEdition = wordObject.word
        binding.editTextWordEdition.setText(wordForEdition)

    }

    companion object {
        private const val TAG_WORD_ID = "word_id"
        fun newIntent(context: Context, wordId: Int): Intent {
            return Intent(context, EditWordActivity::class.java)
                .apply { putExtra(TAG_WORD_ID, wordId) }
        }
    }

    fun onCancelChangesClick(view: View) {
        finish()
    }

    fun onConfirmChangesClick(view: View) {
        when (val changedWord = binding.editTextWordEdition.text.toString()) {
            wordForEdition -> {
                Toast.makeText(view.context, "The word wasn't changed", Toast.LENGTH_SHORT).show()
            }
            "" -> {
                Toast.makeText(view.context, "Type a word", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val changedWordObject = Word(wordId, wordObject.deckId, changedWord)
                MainPresenter(view.context).updateWord(changedWordObject)
                Toast.makeText(view.context, "The word has been changed", Toast.LENGTH_SHORT).show()
                finish()
            }
        }}
}
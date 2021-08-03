package com.example.milim.presentation.screens.lesson

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.milim.R
import com.example.milim.databinding.ActivityLessonBinding
import com.example.milim.databinding.DialogLessonCounterBinding
import com.example.milim.presentation.fragments.AdditionWordFragment
import com.example.milim.presentation.fragments.DeletingWordFragment
import com.example.milim.presentation.fragments.EditionWordFragment
import com.example.milim.interfaces.OnActionPerformedUpdater
import com.example.milim.domain.pojo.Deck
import com.example.milim.domain.pojo.Word
import com.example.milim.interfaces.LessonView

class LessonActivity : AppCompatActivity(),
    DeletingWordFragment.ListenerCallback,
    EditionWordFragment.ListenerCallback,
    AdditionWordFragment.ListenerCallback,
    OnActionPerformedUpdater,
    LessonView {

    private lateinit var binding: ActivityLessonBinding
    private lateinit var deck: Deck
    private var deckId = -1
    private var deckName = ""
    private var lessonProgress = 0
    private var wordIndex = 0
    private lateinit var words: MutableList<Word>
    private lateinit var presenter: LessonPresenter

    companion object {
        private const val TAG_WORD_ADDITION_DIALOG = "word_addition_dialog"
        private const val TAG_WORD_DELETING_DIALOG = "word_deleting_dialog"
        private const val TAG_WORD_EDITION_DIALOG = "word_edition_dialog"
        private const val TAG_DECK_ID = "deck_id"
        fun newIntent(context: Context, deck_id: Int): Intent {
            return Intent(context, LessonActivity::class.java).apply {
                putExtra(
                    TAG_DECK_ID,
                    deck_id
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        presenter = LessonPresenter(this, applicationContext)
        val intent = intent
        deckId = intent.getIntExtra(TAG_DECK_ID, -1)
        words = mutableListOf()

        presenter.loadData(deckId)

        binding.textViewDeckName.text = deckName
        binding.textViewQuantity.text = words.size.toString()
        binding.linearLayoutCounter.setOnLongClickListener {
            showCounterDialog(it.context)
            true
        }
    }

    override fun setContent(wordsFromDB: List<Word>, deckFromDB: Deck) {
        words.clear()
        words.addAll(wordsFromDB)
        deck = deckFromDB
        deckName = deck.name
        setLessonProgressOnStartLesson()
        setWordIndexOnStartLesson()
        setViewContent()
        setOnLongClickListenerAccordingToWordsList()
    }

    override fun updateContent(wordsFromDB: List<Word>, deck: Deck) {
        words.clear()
        words.addAll(wordsFromDB)
        lessonProgress = deck.progress
        setViewContent()
        setOnLongClickListenerAccordingToWordsList()
    }

    override fun onUpdateDeck(deck: Deck) {
        lessonProgress = deck.progress
        setViewContent()
        setOnLongClickListenerAccordingToWordsList()
    }

    override fun onActionPerformedRefresh() {
        presenter.reloadData(deckId)
    }

    override fun onConformWordAddition(newWord: String, deckId: Int) {
        presenter.addWord(newWord, deckId)
    }

    override fun onConfirmWordDeleting() {
        deleteWord(words[wordIndex])
        if (words.size > 1 && lessonProgress == words.size) {
            lessonProgress--
            wordIndex--
        } else if (words.size > 1 && lessonProgress > 1) {
            lessonProgress--
        } else if (words.size == 1) {
            wordIndex = 0
            lessonProgress = 0
        }
        saveProgressOnDeleteWord()
    }

    override fun onConfirmChanges(wordObject: Word) {
        presenter.updateWord(wordObject)
    }

    fun onNextClick(view: View) {
        if (words.isNotEmpty()) {
            wordIndex++
            if (wordIndex == words.size) {
                wordIndex = 0
            }
            updateLessonProgress()
            setViewContent()
            saveProgress()
        }
    }

    fun onBackClick(view: View) {
        if (words.isNotEmpty()) {
            wordIndex--
            if (wordIndex < 0) {
                wordIndex = words.size - 1
            }
            updateLessonProgress()
            setViewContent()
            saveProgress()
        }
    }

    private fun setViewContent() {
        with(binding) {
            textViewDeckName.text = deckName
            if (words.size == 0) {
                textViewLessonWord.text = "The deck is empty"
                textViewProgress.text = (0).toString()
            } else {
                textViewLessonWord.text = words[wordIndex].word
                textViewProgress.text = (lessonProgress).toString()
            }
            textViewQuantity.text = words.size.toString()
        }
    }

    fun onMainButtonClick(view: View) {
        val animationAddButton: Animation
        val animationDeleteButton: Animation
        val clickable: Boolean
        val visibility: Int
        if (binding.buttonAdd.isVisible) {
            animationAddButton =
                AnimationUtils.loadAnimation(applicationContext, R.anim.close_add_button)
            animationDeleteButton =
                AnimationUtils.loadAnimation(applicationContext, R.anim.close_delete_button)
            clickable = false
            visibility = View.INVISIBLE
        } else {
            animationAddButton =
                AnimationUtils.loadAnimation(applicationContext, R.anim.open_add_button)
            animationDeleteButton =
                AnimationUtils.loadAnimation(applicationContext, R.anim.open_delete_button)
            clickable = true
            visibility = View.VISIBLE
        }
        with(binding) {
            buttonAdd.isClickable = clickable
            buttonDelete.isClickable = clickable
            buttonAdd.visibility = visibility
            buttonDelete.visibility = visibility
            buttonDelete.startAnimation(animationDeleteButton)
            buttonAdd.startAnimation(animationAddButton)
        }
    }

    fun onDeleteButtonClick(view: View) {
        if (words.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "there's noting for deleting",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            val dialog = DeletingWordFragment.newInstance(words[wordIndex].wordId)
            dialog.show(supportFragmentManager, TAG_WORD_DELETING_DIALOG)
        }
    }

    fun onAddWordButtonClick(view: View) {
        val dialog = AdditionWordFragment.newInstance(deckId)
        dialog.show(supportFragmentManager, TAG_WORD_ADDITION_DIALOG)
    }

    private fun deleteWord(word: Word) {
        presenter.deleteWord(word)
    }

    private fun saveProgress() {
        presenter.updateDeck(Deck(deckId, deckName, words.size, lessonProgress))
    }

    private fun saveProgressOnDeleteWord() {
        presenter.updateDeck(Deck(deckId, deckName, words.size - 1, lessonProgress))
    }

    private fun updateLessonProgress() {
        lessonProgress = wordIndex + 1
    }

    private fun setWordIndexOnStartLesson() {
        wordIndex = when {
            words.size < 2 -> 0
            lessonProgress == 0 -> 0
            else -> lessonProgress - 1
        }
    }

    private fun setLessonProgressOnStartLesson() {
        lessonProgress = when {
            words.isEmpty() -> 0
            else -> deck.progress
        }
    }

    private fun setOnLongClickListenerAccordingToWordsList() {
        if (words.isNotEmpty()) {
            binding.textViewLessonWord.setOnLongClickListener {
                val dialog = EditionWordFragment.newInstance(words[wordIndex])
                dialog.show(supportFragmentManager, TAG_WORD_EDITION_DIALOG)
                true
            }
        } else {
            binding.textViewLessonWord.setOnLongClickListener(null)
        }
    }

    // is repeated
    private fun showCounterDialog(context: Context) {
        val dialog = Dialog(context)
        val binding = DialogLessonCounterBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(binding.root)
        binding.editTextCounter.setText(lessonProgress.toString())
        binding.editTextCounter.setSelection(binding.editTextCounter.length())
        dialog.show()

        binding.buttonClearCounter.setOnClickListener { binding.editTextCounter.setText("") }
        binding.buttonGoToWord.setOnClickListener {
            val wordNumber = binding.editTextCounter.text.toString().toInt()
            if (wordNumber in 1..words.size) {
                lessonProgress = wordNumber
                wordIndex = lessonProgress - 1
                setViewContent()
                saveProgress()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "There's not word with such number", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}

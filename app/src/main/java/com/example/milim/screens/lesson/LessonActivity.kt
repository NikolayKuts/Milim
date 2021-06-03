package com.example.milim.screens.lesson

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.milim.R
import com.example.milim.databinding.ActivityLessonBinding
import com.example.milim.databinding.DialogLessonCounterBinding
import com.example.milim.databinding.DialogWordDeletingBinding
import com.example.milim.fragments.AdditionWordFragment
import com.example.milim.fragments.DeletingWordFragment
import com.example.milim.pojo.Deck
import com.example.milim.pojo.Word
import com.example.milim.screens.edition_word.EditWordActivity
//import com.example.milim.screens.adding_word.AddWordActivity
import com.example.milim.screens.main.MainPresenter

class LessonActivity : AppCompatActivity(),
    AdditionWordFragment.OnDialogFragmentClosedListener,
    DeletingWordFragment.OnDialogFragmentClosedListener {

    private lateinit var binding: ActivityLessonBinding
    private lateinit var deck: Deck
    private var deckId = -1
    private var deckName = ""
    private var lessonProgress = 0
    private var wordIndex = 0
    private lateinit var words: MutableList<Word>

    companion object {
        private const val TAG_ADDITION_WORD_DIALOG = "addition_word_dialog"
        private const val TAG_DELETING_WORD_DIALOG = "deleting_word_dialog"
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

        val presenter = MainPresenter(applicationContext)
        val intent = intent
        deckId = intent.getIntExtra(TAG_DECK_ID, -1)
        words = presenter.getWords(deckId).toMutableList()
        deck = presenter.getDeckById(deckId)
        deckName = deck.name
        setLessonProgressOnStartLesson()
        setWordIndexOnStartLesson()
        setViewContent()


        binding.textViewDeckName.text = deckName
        binding.textViewQuantity.text = words.size.toString()
        binding.linearLayoutConter.setOnLongClickListener {
            showCounterDialog(it.context)
            true
        }





        for (word in words) {
            Log.i("my_test", "onCreate: $word")
        }
    }

    override fun onResume() {
        super.onResume()
        val presenter = MainPresenter(applicationContext)
        updateWordList()
        deck = presenter.getDeckById(deckId)
        setLessonProgressOnStartLesson()
        setViewContent()
        if (words.isNotEmpty()) {
            binding.textViewWord.setOnLongClickListener {
                startActivity(EditWordActivity.newIntent(it.context, words[wordIndex].wordId))
                true
            }
        }
    }

    override fun onAddWordRefreshData() {
        onResume()
    }

    override fun onDeleteWordRefreshData() {
        onConfirmDeleting()
        updateLessonProgress()
        saveProgress()
        updateWordList()
        setViewContent()
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
        showWordIndexProgress()
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
        showWordIndexProgress()
    }

    private fun setViewContent() {
        with(binding) {
            if (words.size == 0) {
                textViewWord.text = "The deck is empty"
                textViewProgress.text = (0).toString()
            } else {
                textViewWord.text = words[wordIndex].word
                textViewProgress.text = (lessonProgress).toString()
            }
            textViewQuantity.text = words.size.toString()
        }

        for (word in words) {
            Log.i("my_test", "onCreate: $word")
        }
        Log.i("my_test", "onCreate: ------------------------------")
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
        //showWordDeletingDialog(view)
        if (words.isEmpty()) {
            Toast.makeText(applicationContext, "there's noting for deleting", Toast.LENGTH_SHORT)
                .show()
        } else {
            val dialog = DeletingWordFragment.newInstance(words[wordIndex].wordId)
            dialog.show(supportFragmentManager, TAG_DELETING_WORD_DIALOG)
        }
    }

    fun onAddWordButtonClick(view: View) {
//        startActivity(AddWordActivity.newIntent(view.context, deckId))

        val dialog = AdditionWordFragment.newInstance(deckId)
        dialog.show(supportFragmentManager, TAG_ADDITION_WORD_DIALOG)
    }


    private fun showWordDeletingDialog(view: View) {
        val dialog = Dialog(view.context)
        dialog.setContentView(R.layout.dialog_deck_deleting)
        val binding = DialogWordDeletingBinding.inflate(LayoutInflater.from(view.context))
        dialog.setContentView(binding.root)
        if (words.isEmpty()) {
            Toast.makeText(applicationContext, "there's noting for deleting", Toast.LENGTH_SHORT)
                .show()
        } else {
            dialog.show()
        }

        binding.buttonConfirmWordDeleting.setOnClickListener {
            onConfirmDeleting()
            updateLessonProgress()
            saveProgress()
            updateWordList()
            setViewContent()
            dialog.dismiss()
        }
        binding.buttonCancelDeleting.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun onConfirmDeleting() {
        if (words.size > 1 && lessonProgress == 1) {
            deleteWord((words[wordIndex]))
        } else if (words.size > 1 && lessonProgress == words.size) {
            deleteWord(words[wordIndex])
            lessonProgress--
            wordIndex--
        } else if (words.size > 1 && lessonProgress > 1) {
            deleteWord(words[wordIndex])
            lessonProgress--
        } else if (words.size == 1) {
            deleteWord(words[wordIndex])
            wordIndex = 0
            lessonProgress = 0
        }
        showWordIndexProgress()
    }

    private fun updateWordList() {
        words.clear()
        words.addAll(MainPresenter(applicationContext).getWords(deckId))
    }

    private fun deleteWord(word: Word) {
        val presenter = MainPresenter(applicationContext)
        presenter.deleteWord(word)
    }

    private fun saveProgress() {
        val presenter = MainPresenter(applicationContext)
        presenter.updateDeck(Deck(deckId, deckName, words.size, lessonProgress))
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
//            words.isNotEmpty() && lessonProgress == 0 -> 1
            else -> deck.progress
        }
    }

    private fun showWordIndexProgress() {
        Log.i(
            "my_test",
            "showWordIndexProgress: word index -> $wordIndex progress -> $lessonProgress"
        )
    }

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

    private fun showEditionWordDialog(context: Context) {
        val dialog = AdditionWordFragment()
        dialog.show(supportFragmentManager, TAG_ADDITION_WORD_DIALOG)
    }
}

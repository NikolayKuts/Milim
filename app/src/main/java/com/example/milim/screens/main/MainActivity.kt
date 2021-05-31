package com.example.milim.screens.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.milim.R
import com.example.milim.adapters.DeckAdapter
import com.example.milim.databinding.ActivityMainBinding
import com.example.milim.databinding.DialogDeckBinding
import com.example.milim.databinding.DialogDeckCreationBinding
import com.example.milim.databinding.DialogDeckDeletingBinding
import com.example.milim.pojo.Deck
import com.example.milim.pojo.Word
import com.example.milim.screens.lesson.LessonActivity
import com.example.milim.screens.word_browser.WordBrowserActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var decks: MutableList<Deck>
    private lateinit var adapter: DeckAdapter

    companion object {
        const val FILE_NAME = "object_test.obj"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val presenter = MainPresenter(this.applicationContext)
        decks = presenter.getAllDecks().toMutableList()

        val recyclerView = binding.recyclerViewDecks
        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext)
        adapter = DeckAdapter(decks, applicationContext)
        recyclerView.adapter = adapter

        adapter.onDeckLongClickListener = DeckAdapter.OnDeckLongClickListener {
            showDeckDialog(view.context, it)
        }
        adapter.onDeckClickListener = object : DeckAdapter.OnDeckClickListener {
            override fun onClick(deck: Deck) {
                startActivity(LessonActivity.newIntent(this@MainActivity, deck.id))
            }
        }

//        val someObject = presenter.getDeckById(decks[0].id)
//        val outputStream = openFileOutput(FILE_NAME, MODE_PRIVATE)
//        val objectOutPutStream = ObjectOutputStream(outputStream)
//        objectOutPutStream.writeObject(someObject)
//        Log.i("file_dir", "onCreate: $filesDir")

//        val assetsManager = applicationContext.assets
//        val inputStream = assetsManager.open(FILE_NAME)
//        val objectOutputStream = ObjectInputStream(inputStream)
//        val someDeck = objectOutputStream.readObject() as Deck
//        Log.i("file_dir", "name: ${someDeck.name}, id: ${someDeck.id}, size: ${someDeck.size} progress: ${someDeck.progress}")


//        val list = getString(R.string.string_of_words1).split("|_|").take(10)
//
//        presenter.addDeck(Deck(1, "Deck_1"))
//
//        var wordId = 1
//        val words = list.map { Word(wordId++, 1, it) }.take(5)
//        presenter.addWordList(words)
//        adapter.notifyDataSetChanged()


    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onResume() {
        super.onResume()
        val presenter = MainPresenter(this.applicationContext)
        decks.clear()
        decks.addAll(presenter.getAllDecks())
        adapter.notifyDataSetChanged()
    }

    fun onMainActivityButtonClick(view: View) {
        showDeckCreationDialog(view.context)
    }

    private fun showDeckCreationDialog(context: Context) {
        val dialog = Dialog(context)
        val binding = DialogDeckCreationBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val dialogView = binding.root
        dialog.setContentView(dialogView)
        binding.editTextDeckName.setText("")
        dialog.show()
        binding.buttonCancelDeckCreation.setOnClickListener { dialog.dismiss() }
        binding.buttonCreateDeck.setOnClickListener {
            val deckName = binding.editTextDeckName.text.toString().trim()
            if (deckName != "" && isDeckNotExist(deckName)) {
                onCreationConfirming(deckName)
                dialog.dismiss()
                onResume()
                Toast.makeText(applicationContext, "the deck has been created", Toast.LENGTH_LONG)
                    .show()
            } else {
                when (deckName) {
                    "" -> Toast.makeText(applicationContext, "Type deck name", Toast.LENGTH_LONG)
                        .show()
                    else -> Toast.makeText(applicationContext, "the deck is already exist", Toast.LENGTH_LONG)
                        .show()
                }

            }
        }
    }

    private fun onCreationConfirming(deckName: String) {
        val presenter = MainPresenter(applicationContext)
        val newDeckId = presenter.getMaxDeckId() + 1
        val newDeck = Deck(newDeckId, deckName)
        presenter.addDeck(newDeck)
    }

    private fun isDeckNotExist(name: String): Boolean {
        val presenter = MainPresenter(applicationContext)
        return !presenter.isDeckExist(name)
    }

    private fun showDeckDialog(context: Context, deck: Deck) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_deck)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val textViewDeckDialogTitle = dialog.findViewById<TextView>(R.id.textViewDeckDialogTitle)
        val buttonDeleteDeck = dialog.findViewById<Button>(R.id.buttonDeleteDeck)
        val buttonShowWordList = dialog.findViewById<Button>(R.id.button_show_word_list)

        textViewDeckDialogTitle.text = deck.name
        buttonDeleteDeck.setOnClickListener {
            showDeckDeletingDialog(context, deck)
            dialog.dismiss()
        }
        buttonShowWordList.setOnClickListener {
            startActivity(WordBrowserActivity.newIntent(applicationContext, deck.id))
        }

        dialog.show()
    }

    private fun showDeckDeletingDialog(context: Context, deck: Deck) {
        val dialog = Dialog(context)
        val binding = DialogDeckDeletingBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val view = binding.root
        dialog.setContentView(view)
        dialog.show()
        binding.textViewDeckDeletingTitle.text = "Would you like to delete the deck \"${deck.name}\"?"
        binding.buttonCancelDeleting.setOnClickListener { dialog.dismiss() }
        binding.buttonDeleteDeck.setOnClickListener {
            val presenter = MainPresenter(context)
            presenter.deleteDeck(deck)
            dialog.dismiss()
            onResume()
            Toast.makeText(context, "The deck has been deleted", Toast.LENGTH_SHORT)
                .show()
        }
    }

}
package com.example.milim.presentation.screens.main

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.milim.R
import com.example.milim.presentation.adapters.DeckAdapter
import com.example.milim.databinding.*
import com.example.milim.presentation.fragments.DeckRenamingFragment
import com.example.milim.presentation.interfaces.OnActionPerformedUpdater
import com.example.milim.domain.pojo.Deck
import com.example.milim.presentation.interfaces.MainView
import com.example.milim.presentation.screens.lesson.LessonActivity
import com.example.milim.presentation.screens.word_browser.WordBrowserActivity
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MainActivity : AppCompatActivity(),
    OnActionPerformedUpdater,
    DeckRenamingFragment.ListenerCallback,
    MainView {

    private lateinit var binding: ActivityMainBinding
    private lateinit var decks: MutableList<Deck>
    private lateinit var adapter: DeckAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: MainPresenter

    companion object {
        const val FILE_NAME = "object_test.obj"
        const val TAG_DECK_RENAMING_DIALOG = "deck_renaming_dialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        recyclerView = binding.recyclerViewDecks
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        presenter = MainPresenter(this, applicationContext)

        decks = mutableListOf()

        adapter = DeckAdapter(decks, applicationContext)
        recyclerView.adapter = adapter

        adapter.onDeckLongClickListener = DeckAdapter.OnDeckLongClickListener { deck ->
            showDeckDialog(view.context, deck)
        }
        adapter.onDeckClickListener = DeckAdapter.OnDeckClickListener { deck ->
            startActivity(LessonActivity.newIntent(this@MainActivity, deck.id))
        }
    }

    override fun showData(decksFromDB: List<Deck>) {
        decks.clear()
        decks.addAll(decksFromDB)
        adapter.notifyDataSetChanged()
    }

    override fun onActionPerformedRefresh() {
        onResume()
    }

    override fun onConformDeckRenaming(oldDeck: Deck, newDeck: Deck) {
        presenter.renameDeck(oldDeck, newDeck)
    }

    override fun onResume() {
        super.onResume()
        presenter.loadData()
    }

    override fun showToastIfDeckExist() {
        Toast.makeText(
            applicationContext,
            getString(R.string.toast_the_deck_is_already_exist),
            Toast.LENGTH_LONG
        )
            .show()
    }

    override fun showToastOnDeckCreated() {
        Toast.makeText(
            applicationContext,
            getString(R.string.toast_the_deck_has_been_created),
            Toast.LENGTH_LONG
        )
            .show()
    }


    override fun showToastOnDeckDeleted() {
        Toast.makeText(
            applicationContext,
            getString(R.string.toast_the_deck_has_been_deleted),
            Toast.LENGTH_SHORT
        )
            .show()
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
            if (deckName.isNotEmpty()) {
                presenter.onAddDeck(deckName) { dialog.dismiss() }
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toast_type_deck_name),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    private fun showDeckDialog(context: Context, deck: Deck) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_deck)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val textViewDeckDialogTitle = dialog.findViewById<TextView>(R.id.textViewDeckDialogTitle)
        val buttonDeleteDeck = dialog.findViewById<Button>(R.id.buttonDeleteDeck)
        val buttonShowWordList = dialog.findViewById<Button>(R.id.button_show_word_list)
        val buttonRenameDeck = dialog.findViewById<Button>(R.id.button_rename_deck)

        textViewDeckDialogTitle.text = deck.name

        buttonDeleteDeck.setOnClickListener {
            showDeckDeletingDialog(context, deck)
            dialog.dismiss()
        }
        buttonShowWordList.setOnClickListener {
            if (deck.size != 0) {
                startActivity(WordBrowserActivity.newIntent(applicationContext, deck.id))
                dialog.dismiss()
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toast_there_are_no_words_in_this_deck),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        buttonRenameDeck.setOnClickListener {
            val wordRenamingDialog = DeckRenamingFragment.newInstance(deck)
            wordRenamingDialog.show(supportFragmentManager, TAG_DECK_RENAMING_DIALOG)
            dialog.dismiss()
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
        binding.textViewDeckDeletingTitle.text =
            getString(R.string.toast_would_you_like_to_delete_the_deck, deck.name)
        binding.buttonCancelDeleting.setOnClickListener { dialog.dismiss() }
        binding.buttonDeleteDeck.setOnClickListener {
            presenter.deleteDeck(deck)
            dialog.dismiss()
        }
    }

    private fun getDeckFromAssets(): Deck {
        val assetsManager = applicationContext.assets
        val inputStream = assetsManager.open(FILE_NAME)
        val objectOutputStream = ObjectInputStream(inputStream)
        return objectOutputStream.readObject() as Deck
    }

    private fun writeDeckIntoFile(deck: Deck) {
        val outputStream = openFileOutput(FILE_NAME, MODE_PRIVATE)
        val objectOutPutStream = ObjectOutputStream(outputStream)
        objectOutPutStream.writeObject(deck)
    }

}
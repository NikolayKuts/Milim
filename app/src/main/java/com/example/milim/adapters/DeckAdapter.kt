package com.example.milim.adapters

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HeaderViewListAdapter
import android.widget.TextView
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.milim.R
import com.example.milim.databinding.ActivityMainBinding
import com.example.milim.databinding.ItemDeckBinding
import com.example.milim.pojo.Deck
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.Inflater

class DeckAdapter(var decks: List<Deck>, val context: Context) :
    RecyclerView.Adapter<DeckAdapter.DeckViewHolder>() {

    var onDeckLongClickListener: OnDeckLongClickListener? = null
    var onDeckClickListener: OnDeckClickListener? = null
    private val currentLanguage = ConfigurationCompat.getLocales(
        context.resources.configuration
    )[0].language

    companion object {
        private const val HEBREW_LETTERS = "אבגדהוזחטיכךלמםנןסעפףצץקרשת"
        private const val HEBREW_LANGUAGE = "iw"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_deck, parent, false)
        return DeckViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = decks[position]
        with(holder) {
            textViewName.text = deck.name
            setTextViewNameGravity(deck, textViewName)
            textViewProgress.text = deck.progress.toString()
            textViewSize.text = deck.size.toString()
            constraintLayout.setOnLongClickListener {
                onDeckLongClickListener?.onLongClick(deck)
                true
            }
            constraintLayout.setOnClickListener {
                onDeckClickListener?.onClick(deck)
            }
        }
    }

    override fun getItemCount(): Int {
        return decks.size
    }

    private fun setTextViewNameGravity(deck: Deck, textViewName: TextView) {
        val pattern = Pattern.compile("[!$HEBREW_LETTERS]")
        val match = pattern.matcher(deck.name).find()

        textViewName.gravity = when (currentLanguage) {
            HEBREW_LANGUAGE -> {
                when {
                    (match) -> Gravity.START or Gravity.CENTER_VERTICAL
                    else -> Gravity.END or Gravity.CENTER_VERTICAL
                }
            }
            else -> {
                when {
                    (match) -> Gravity.END or Gravity.CENTER_VERTICAL
                    else -> Gravity.START or Gravity.CENTER_VERTICAL
                }
            }
        }
    }

    inner class DeckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemDeckBinding.bind(itemView)
        val textViewName = binding.textViewItemDeckName
        val textViewProgress = binding.textViewItemProgress
        val textViewSize = binding.textViewSize
        val constraintLayout = binding.itemDeck
    }

    fun interface OnDeckLongClickListener {
        fun onLongClick(deck: Deck)
    }

    fun interface OnDeckClickListener {
        fun onClick(deck: Deck)
    }
}
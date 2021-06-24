package com.example.milim.presentation.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.milim.databinding.ItemDeckBinding
import com.example.milim.domain.pojo.Deck
import java.util.regex.Pattern

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
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDeckBinding.inflate(inflater, parent, false)
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = decks[position]
        holder.binding.apply {
            textViewItemDeckName.text = deck.name
            setTextViewNameGravity(deck, textViewItemDeckName)
            textViewItemProgress.text = deck.progress.toString()
            textViewSize.text = deck.size.toString()
            itemDeck.setOnLongClickListener {
                onDeckLongClickListener?.onLongClick(deck)
                true
            }
            itemDeck.setOnClickListener {
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

    inner class DeckViewHolder(val binding: ItemDeckBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    fun interface OnDeckLongClickListener {
        fun onLongClick(deck: Deck)
    }

    fun interface OnDeckClickListener {
        fun onClick(deck: Deck)
    }
}
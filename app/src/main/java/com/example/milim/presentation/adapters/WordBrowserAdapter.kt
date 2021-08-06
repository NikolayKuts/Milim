package com.example.milim.presentation.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.milim.databinding.ItemWordBrowserBinding
import com.example.milim.domain.pojo.Word
import java.util.regex.Pattern

class WordBrowserAdapter(context: Context) :
    RecyclerView.Adapter<WordBrowserAdapter.ViewHolder>() {
    var words = listOf<Word>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val currentLanguage = ConfigurationCompat.getLocales(
        context.resources.configuration
    )[0].language

    companion object {
        private const val HEBREW_LETTERS = "אבגדהוזחטיכךלמםנןסעפףצץקרשת"
        private const val HEBREW_LANGUAGE = "iw"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWordBrowserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wordObject = words[position]
        with(holder.binding) {
            textViewWordNumber.text = (position + 1).toString()
            textViewWord.text = words[position].word

            val pattern = Pattern.compile("[!$HEBREW_LETTERS]")
            val match = pattern.matcher(wordObject.word).find()

            textViewWord.gravity = when (currentLanguage) {
                HEBREW_LANGUAGE -> {
                    when {
                        (match) -> Gravity.END or Gravity.CENTER_VERTICAL
                        else -> Gravity.START or Gravity.CENTER_VERTICAL
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
    }

    override fun getItemCount(): Int {
        return words.size
    }

    inner class ViewHolder(val binding: ItemWordBrowserBinding) :
        RecyclerView.ViewHolder(binding.root)
}
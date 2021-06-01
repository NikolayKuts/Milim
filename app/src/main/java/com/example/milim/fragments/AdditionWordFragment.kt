package com.example.milim.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.milim.R
import com.example.milim.databinding.DialogFragmentAddWordBinding
import com.example.milim.screens.main.MainPresenter

class AdditionWordFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentAddWordBinding
    private lateinit var listener: OnDialogFragmentClosedListener
    private var deckId = -1;

    companion object {
        private const val TAG_DECK_ID = "deck_id"
        fun newInstance(deckId: Int): AdditionWordFragment {
            return AdditionWordFragment().apply {
                arguments = bundleOf(
                    TAG_DECK_ID to deckId
                )
            }
        }
    }

    interface OnDialogFragmentClosedListener {
        fun refreshData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnDialogFragmentClosedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentAddWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            deckId = it.getInt(TAG_DECK_ID)
        }

        binding.buttonCancelWordAdding.setOnClickListener {
            dismiss()
        }

        binding.buttonAddWord.setOnClickListener {
            val newWord = binding.editTextNewWord.text.toString()
            if (newWord != "") {
                insetWord(it.context, newWord, deckId)
                binding.editTextNewWord.setText("")
                Toast.makeText(it.context, "The word has been added", Toast.LENGTH_SHORT).show()
                listener?.refreshData()
                dismiss()
            } else {
                Toast.makeText(it.context, "Type a word", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun insetWord(context: Context, newWord: String, deckId: Int) {
        val presenter = MainPresenter(context)
        presenter.addWord(newWord, deckId)
    }
}
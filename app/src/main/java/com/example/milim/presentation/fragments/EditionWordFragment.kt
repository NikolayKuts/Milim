package com.example.milim.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.milim.databinding.DialogFragmentWordEditionBinding
import com.example.milim.interfaces.OnActionPerformedUpdater
import com.example.milim.domain.pojo.Word

class EditionWordFragment : DialogFragment() {
    private var _binding: DialogFragmentWordEditionBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var listener: ListenerCallback
    private lateinit var dataUpdater: OnActionPerformedUpdater

    companion object {
        private const val TAG_WORD_OBJECT = "word_object"
        fun newInstance(word: Word): EditionWordFragment {
            return EditionWordFragment().apply {
                arguments = bundleOf().apply {
                    putParcelable(TAG_WORD_OBJECT, word)
                }
            }
        }
    }

    interface ListenerCallback {
        fun onConfirmChanges(wordObject: Word)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as ListenerCallback
        dataUpdater = context as OnActionPerformedUpdater
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        _binding = DialogFragmentWordEditionBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var wordObject: Word? = null
        arguments?.let {
            wordObject = it.getParcelable(TAG_WORD_OBJECT)
            wordObject?.let { obj ->
                dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                binding.editTextEditionWord.setText(obj.word)
                binding.editTextEditionWord.requestFocus()
            }
        }

        binding.buttonConfirmChanges.setOnClickListener {
            when (val changedWord = binding.editTextEditionWord.text.toString().trim()) {
                wordObject?.word -> {
                    Toast.makeText(it.context, "The word isn't changed", Toast.LENGTH_SHORT)
                        .show()
                }
                "" -> {
                    Toast.makeText(it.context, "Type a word", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    wordObject?.let { word ->
                        listener.onConfirmChanges(Word(word.wordId, word.deckId, changedWord))
                        dataUpdater.onActionPerformedRefresh()
                        dismiss()
                        Toast.makeText(view.context, "The word has been changed", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

        binding.buttonCancelChanges.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}
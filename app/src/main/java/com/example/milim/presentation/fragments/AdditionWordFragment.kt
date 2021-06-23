package com.example.milim.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.milim.databinding.DialogFragmentWordAdditionBinding
import com.example.milim.interfaces.OnActionPerformedUpdater
import com.example.milim.presentation.screens.main.MainPresenter

class AdditionWordFragment : DialogFragment() {
    private var _binding: DialogFragmentWordAdditionBinding? = null
    private val binding
    get() = _binding!!
    private lateinit var dataUpdater: OnActionPerformedUpdater
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataUpdater = context as OnActionPerformedUpdater
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        _binding = DialogFragmentWordAdditionBinding.inflate(inflater, container, false)
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
                dataUpdater.onActionPerformedRefresh()
                dismiss()
            } else {
                Toast.makeText(it.context, "Type a word", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun insetWord(context: Context, newWord: String, deckId: Int) {
        val presenter = MainPresenter(context)
        presenter.addWord(newWord, deckId)
    }
}
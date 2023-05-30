package com.example.milim.presentation.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.milim.R
import com.example.milim.databinding.DialogFragmentDeckRenamingBinding
import com.example.milim.presentation.interfaces.OnActionPerformedUpdater
import com.example.milim.domain.pojo.Deck

class DeckRenamingFragment : DialogFragment() {
    private var _binding: DialogFragmentDeckRenamingBinding? = null
    private val binding get() = _binding!!
    private var dataUpdater: OnActionPerformedUpdater? = null
    private var listener: ListenerCallback? = null

    companion object {
        private const val TAG_DECK = "deck_id"
        fun newInstance(deck: Deck): DeckRenamingFragment {
            return DeckRenamingFragment().apply {
                arguments = bundleOf().apply {
                    putSerializable(TAG_DECK, deck)
                }
            }
        }
    }

    interface ListenerCallback {
        fun onConformDeckRenaming(oldDeck: Deck, newDeck: Deck)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ListenerCallback) {
            listener = context
        }
        if (context is OnActionPerformedUpdater) {
            dataUpdater = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        _binding = DialogFragmentDeckRenamingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var oldDeck: Deck? = null
        arguments?.let {
            oldDeck = it.getSerializable(TAG_DECK) as Deck
            oldDeck?.let { deck ->
                dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
                binding.editTextDeckRenaming.setText(deck.name)
                binding.editTextDeckRenaming.requestFocus()
            }
        }

        binding.buttonCancelDeckRenaming.setOnClickListener { dismiss() }

        binding.buttonConformDeckRenaming.setOnClickListener { button ->
            oldDeck?.let { OldDeck ->
                when (val changedDeckName = binding.editTextDeckRenaming.text.toString()) {
                    OldDeck.name -> {
                        Toast.makeText(
                            button.context,
                            getString(R.string.toast_the_old_deck_isnt_changed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    "" -> {
                        Toast.makeText(button.context, "", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        listener?.onConformDeckRenaming(
                            OldDeck,
                            Deck(
                                id = OldDeck.id,
                                name = changedDeckName,
                                size = OldDeck.size,
                                progress = OldDeck.progress
                            )
                        )
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        _binding = null
        super.onDismiss(dialog)
    }
}
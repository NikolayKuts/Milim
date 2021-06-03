package com.example.milim.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.milim.databinding.DialogFragmentDeletingWordBinding
import com.example.milim.screens.main.MainPresenter

class DeletingWordFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentDeletingWordBinding
    private lateinit var listener: OnDialogFragmentClosedListener
    private var wordId = -1

    companion object {
        private const val TAG_WORD_ID = "word_id"
        fun newInstance(wordId: Int): DeletingWordFragment {
            return DeletingWordFragment().apply {
                arguments = bundleOf(
                    TAG_WORD_ID to wordId
                )
            }
        }
    }
    interface OnDialogFragmentClosedListener {
        fun onDeleteWordRefreshData()
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
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogFragmentDeletingWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            wordId = it.getInt(TAG_WORD_ID)
        }

        binding.buttonConfirmWordDeleting.setOnClickListener {
            deleteWord(view.context)
            listener.onDeleteWordRefreshData()
            dismiss()
        }

        binding.buttonCancelDeleting.setOnClickListener {
            dismiss()
        }

    }

    private fun deleteWord(context: Context) {
        val presenter = MainPresenter(context)
        presenter.deleteWord(wordId)
    }
}
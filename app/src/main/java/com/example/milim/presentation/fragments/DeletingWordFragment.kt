package com.example.milim.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.milim.databinding.DialogFragmentWordDeletingBinding
import com.example.milim.presentation.interfaces.OnActionPerformedUpdater

class DeletingWordFragment : DialogFragment() {
    private var _binding: DialogFragmentWordDeletingBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: ListenerCallback
    private lateinit var dataUpdater: OnActionPerformedUpdater

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

    interface ListenerCallback {
        fun onConfirmWordDeleting()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ListenerCallback) { listener = context }
        if (context is OnActionPerformedUpdater) { dataUpdater = context }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        _binding = DialogFragmentWordDeletingBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonConfirmWordDeleting.setOnClickListener {
            listener.onConfirmWordDeleting()
            dataUpdater.onActionPerformedRefresh()
            dismiss()
        }

        binding.buttonCancelDeleting.setOnClickListener { dismiss() }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
package com.kekadoc.test.twitch.streams.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.kekadoc.test.twitch.streams.R
import com.kekadoc.test.twitch.streams.databinding.DialogFeedbackBinding
import com.kekadoc.test.twitch.streams.model.Feedback
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DialogFeedback : DialogFragment() {

    companion object {
        const val RC_RESULT = "Feedback"
        const val KEY_RESULT = "Feedback"
    }

    private lateinit var binding: DialogFeedbackBinding

    private fun close() {
        val activity = requireActivity()
        if (activity is Navigation)
            activity.navigate(R.id.action_destination_feedback_to_destination_main)
        else dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSend.setOnClickListener {
            Feedback(
                stars = binding.rating.numStars,
                message = binding.editText.text?.toString() ?: "").runCatching {
                Json.encodeToString(this)
            }.onSuccess {
                requireActivity().supportFragmentManager.setFragmentResult(RC_RESULT, bundleOf(KEY_RESULT to it))
                close()
            }.onFailure {
                close()
            }
        }
        binding.materialToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener {
            close()
        }

    }

}
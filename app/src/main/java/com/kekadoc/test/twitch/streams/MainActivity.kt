package com.kekadoc.test.twitch.streams

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.NavController
import com.kekadoc.test.twitch.streams.databinding.ActivityMainBinding
import com.kekadoc.test.twitch.streams.model.Feedback
import com.kekadoc.test.twitch.streams.ui.DialogFeedback
import com.kekadoc.test.twitch.streams.ui.Navigation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity(), Navigation {

    companion object {
        private const val TAG: String = "MainActivity-TAG"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    fun sendFeedback(feedback: Feedback) {
        Log.e(TAG, "sendFeedback: $feedback")
        // TODO: 09.06.2021 Feedback
    }

    override fun navigate(id: Int, data: Bundle) {
        navController.navigate(id, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.setFragmentResultListener(DialogFeedback.RC_RESULT, this, { _, result ->
            result.getString(DialogFeedback.KEY_RESULT, null).runCatching {
                return@runCatching Json.decodeFromString<Feedback>(this)
            }.onSuccess {
                sendFeedback(it)
            }
        })

        binding.toolbar.menu.findItem(R.id.action_feedback).setOnMenuItemClickListener {
            navController.navigate(R.id.action_destination_main_to_destination_feedback)
            true
        }

        navController = findNavController(R.id.nav_host_fragment_content_main)

    }

}
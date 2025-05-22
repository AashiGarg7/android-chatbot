//package com.example.chatmate
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
////import androidx.compose.material3.Text
////import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
////import androidx.compose.ui.tooling.preview.Preview
//import com.example.chatmate.ui.theme.ChatMateTheme
//import androidx.lifecycle.ViewModelProvider
//
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
//        setContent {
//            ChatMateTheme{
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    ChatPage(modifier = Modifier.padding(innerPadding),chatViewModel)
//                }
//            }
//        }
//    }
//}

package com.example.chatmate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.chatmate.ui.theme.ChatMateTheme

class MainActivity : ComponentActivity() {

    // ✅ 1. Declare the voice launcher and view model
    private lateinit var voiceLauncher: ActivityResultLauncher<Intent>
    private lateinit var chatViewModel: ChatViewModel

    // ✅ 2. Main lifecycle function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // ✅ Initialize ViewModel
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        // ✅ Register ActivityResult launcher for voice input
        voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                spokenText?.let {
                    chatViewModel.sendMessage(it) // 🎙️ Send voice text to chat model
                }
            }
        }

        // ✅ Set up Compose UI
        setContent {
            ChatMateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatPage(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = chatViewModel,
                        /*onMicClick = { startVoiceRecognition(this, voiceLauncher) } // ⬅️ Trigger speech input*/
                    )
                }
            }
        }
    }

    // ✅ 3. Voice input helper function
    private fun startVoiceRecognition(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")

        try {
            launcher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(activity, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }
}




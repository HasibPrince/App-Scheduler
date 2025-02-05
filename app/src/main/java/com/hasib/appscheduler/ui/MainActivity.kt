package com.hasib.appscheduler.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hasib.appscheduler.theme.StartupTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Timber.d("onCreate: MainActivity")
        setContent {
            StartupTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    FirstScreen(Modifier.fillMaxSize().padding(it))
                }
            }
        }
    }

    @Composable
    private fun FirstScreen(modifier: Modifier) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello World: FirstScreen")
        }
    }

    @Composable
    private fun SecondScreen(modifier: Modifier) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello World: SecondScreen")
        }
    }
}
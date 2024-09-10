package com.example.aoaappsync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.appsync.interfaces.UpdateCallBack
import com.app.appsync.services.AppsOnAirServices
import com.example.aoaappsync.ui.theme.AOAAppSyncTheme

class MainActivity : ComponentActivity() {
    @Suppress("PrivatePropertyName")
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AOAAppSyncTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        AppsOnAirServices.sync(
            this,
            options = mapOf("showNativeUI" to true),
            callBack = object : UpdateCallBack {
                override fun onSuccess(response: String?) {
                    Log.d(TAG, "onSuccess: ")
                }

                override fun onFailure(message: String?) {
                    Log.d(TAG, "onFailure: ")
                }
            },
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AOAAppSyncTheme {
        Greeting("Android")
    }
}
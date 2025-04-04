package com.precio.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var prices by remember { mutableStateOf<List<Price>?>(null) }

            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                if (prices == null) {
                    LaunchedEffect(Unit) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val price = getPriceForCurrentHour()
                            prices = if (price != null) listOf(price) else emptyList()
                        }
                    }
                    Text("Cargando...")
                } else {
                    if (prices!!.isEmpty()) {
                        Text("No se encontraron precios.")
                    } else {
                        Column {
                            prices!!.forEach { price ->
                                Text("Hora: ${price.time}, Precio: ${price.price}, Color: ${price.color}")
                            }
                        }
                    }
                }
            }
        }
    }
}
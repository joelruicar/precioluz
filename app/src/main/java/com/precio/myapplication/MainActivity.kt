package com.precio.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Párrafo de horas
                            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                                prices!!.forEach { price ->
                                    Text("Hora: ${price.time}")
                                }
                            }

                            // Párrafo de precios
                            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                                prices!!.forEach { price ->
                                    Text("Precio: ${price.price}")
                                }
                            }

                            // Párrafo de colores
                            Column {
                                prices!!.forEach { price ->
                                    Text("Color: ${price.color}")
                                }
                            }
                        }



                    }
                }
            }
        }
    }
}
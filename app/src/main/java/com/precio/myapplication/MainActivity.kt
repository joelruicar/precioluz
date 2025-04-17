package com.precio.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            var currentPrice by remember { mutableStateOf<List<Price>?>(null) }
            var prices by remember { mutableStateOf<List<Price>?>(null) }
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                if (currentPrice == null) {
                    LaunchedEffect(Unit) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val cP = getPriceForCurrentHour()

                            currentPrice = if (cP != null) listOf(cP) else emptyList()
                        }

                    }
                    LaunchedEffect(Unit) {
                        CoroutineScope(Dispatchers.IO).launch {
                            prices = getPricesFromWebsite()
                        }
                    }
                    Text("Cargando...")
                } else {
                    if (currentPrice!!.isEmpty() || prices!!.isEmpty()) {
                        Text("No se encontraron precios.")
                    } else {

                        val calendar = Calendar.getInstance()
                        var currentHour = calendar.get(Calendar.HOUR_OF_DAY).toString()

                        if ( calendar.get(Calendar.HOUR_OF_DAY) == 0) {
                            currentHour = "00"
                        }
                        Column(modifier = Modifier.padding(16.dp)) {

                            Column(modifier = Modifier.padding(bottom = 16.dp, top=16.dp)) {
                                currentPrice!!.forEach { price ->


                                    Text(
                                        text =  "Hora: ${price.time}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )

                                }
                            }

                            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                                currentPrice!!.forEach { price ->

                                    Text(
                                        text =  "Precio: ${price.price}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )

                                }
                            }

                            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                                currentPrice!!.forEach { price ->

                                    val backgroundColor = when {
                                        price.color.contains("bajo") -> Color(0xFF4F7F50) // verde
                                        price.color.contains("medio") -> Color(0xFcc2b107) // amarillo
                                        price.color.contains("alto") -> Color(0xFda44336) // rojo
                                        else -> Color.Transparent
                                    }

                                    Text(
                                        text =  "Color: ${price.color}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(backgroundColor)
                                    )

                                }
                            }

                            Column (modifier = Modifier.padding(bottom = 16.dp)) {
                                Text (
                                    text = "Precios del dia",
                                    color = Color.White,
                                    modifier = Modifier.background(Color.Black)
                                )
                            }

                            Column(modifier = Modifier.padding(bottom = 9.dp)) {
                                prices!!.forEach { price ->
                                    val isCurrentHour = price.time.split(":")[0] == currentHour
                                    val textColor = if (isCurrentHour) Color.White else Color.Black
                                    val backgroundColor = when {
                                        isCurrentHour && price.color.contains("bajo") -> Color(0xFF4F7F50) // verde
                                        isCurrentHour && price.color.contains("medio") -> Color(0xFcc2b107) // amarillo
                                        isCurrentHour && price.color.contains("alto") -> Color(0xFda44336) // rojo
                                        else -> Color.Transparent
                                    }

                                    Text(
                                        text = "Hora: ${price.time}, Precio: ${price.price}, Color: ${price.color}",
                                        color = textColor,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(backgroundColor)
                                    )
                                }
                            }


                        }

                    }
                }
            }
        }
    }
}

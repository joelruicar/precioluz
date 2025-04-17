package com.precio.myapplication

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import java.io.IOException
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.util.Calendar

data class Price(
    var time: String = "",
    var price: String = "",
    var color: String = ""
)

fun getPricesFromWebsite(): List<Price> {
    val priceList = mutableListOf<Price>()

    try {
        val doc = Jsoup.connect("https://tarifaluzhora.es/").get()

        val allSpans = doc.select("span[itemprop='description']").map { it.text() }
        val allPrices = doc.select("span[itemprop='price']").map { it.text() }
        val backgroundColors = doc.select("div.template-tlh__colors--hours-circle").map { element ->
            val classAttribute = element.attr("class")
            extractBackgroundColor(classAttribute)
        }

        val combinedPrices = allSpans.zip(allPrices).zip(backgroundColors) { (time, price), color ->
            Price(time, price, color)
        }

        priceList.addAll(combinedPrices)

    } catch (e: IOException) {
        e.printStackTrace()
    }

    return priceList
}

fun getPriceForCurrentHour(): Price? {
    return try {
        val doc = Jsoup.connect("https://tarifaluzhora.es/").get()

        val allSpans = doc.select("span[itemprop='description']").map { it.text() }
        val allPrices = doc.select("span[itemprop='price']").map { it.text() }
        val backgroundColors = doc.select("div.template-tlh__colors--hours-circle").map { element ->
            val classAttribute = element.attr("class")
            extractBackgroundColor(classAttribute)
        }

        val combinedPrices = allSpans.zip(allPrices).zip(backgroundColors) { (time, price), color ->
            Price(time, price, color)
        }

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        combinedPrices.find { price ->
            val hour = price.time.split(":")[0].toIntOrNull()
            hour == currentHour
        }

    } catch (e: IOException) {

        println("Error de conexión: ${e.message}")
        e.printStackTrace()
        null
    } catch (e: Exception){

        println("Error de conexión: ${e.message}")
        e.printStackTrace()
        null
    }
}

fun extractBackgroundColor(classAttribute: String): String {
    return when {
        classAttribute.contains("template-tlh__background-color-low") -> "bajo"
        classAttribute.contains("template-tlh__background-color-default") -> "medio"
        classAttribute.contains("template-tlh__background-color-high") -> "alto"
        else -> "unknown"
    }
}

class PriceWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val prices = getPricesFromWebsite()
            withContext(Dispatchers.Main) {
                val views = RemoteViews(context.packageName, R.layout.widgetlayout)
                if (prices.isNotEmpty()) {
                    // Actualiza las vistas del widget con los datos
                    views.setTextViewText(R.id.horaTextView, prices[0].time)
                    views.setTextViewText(R.id.precioTextView, prices[0].price)
                    views.setTextViewText(R.id.colorTextView, prices[0].color)
                }
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
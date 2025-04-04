package com.precio.myapplication

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
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
        classAttribute.contains("template-tlh__background-color-low") -> "precio bajo"
        classAttribute.contains("template-tlh__background-color-default") -> "precio medio"
        classAttribute.contains("template-tlh__background-color-high") -> "precio alto"
        else -> "unknown"
    }
}

class MyPriceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val price = getPriceForCurrentHour()
            withContext(Dispatchers.Main) {
                val views = RemoteViews(context.packageName, R.layout.widgetlayout)
                if (price != null) {
                    views.setTextViewText(R.id.horaTextView, price.time)
                    views.setTextViewText(R.id.precioTextView, price.price)
                    views.setTextViewText(R.id.colorTextView, price.color)

                    val backgroundColorRes = when (price.color) {
                        "precio bajo" -> R.color.low_color
                        "precio medio" -> R.color.medium_color
                        "precio alto" -> R.color.high_color
                        else -> android.R.color.transparent
                    }
                    views.setInt(R.id.widgetLayout, "setBackgroundResource", backgroundColorRes)

                } else {
                    views.setTextViewText(R.id.horaTextView, "Error")
                    views.setTextViewText(R.id.precioTextView, "Error")
                    views.setTextViewText(R.id.colorTextView, "Error")
                    views.setInt(R.id.widgetLayout, "setBackgroundResource", android.R.color.white) //Reset the background color
                }
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
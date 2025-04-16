import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.precio.myapplication.MyPriceWidgetProvider
import java.util.Calendar

class UpdateWidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "UPDATE_WIDGET") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context,
                MyPriceWidgetProvider::class.java))
            MyPriceWidgetProvider().onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    companion object {
        fun scheduleWidgetUpdate(context: Context) {

            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, UpdateWidgetReceiver::class.java)
                intent.action = "UPDATE_WIDGET"
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                } else {
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()

                // Set the alarm to trigger at the start of the next hour
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.add(Calendar.HOUR_OF_DAY, 1)

                val triggerAtMillis = calendar.timeInMillis
                val intervalMillis = AlarmManager.INTERVAL_HOUR

                alarmManager.setRepeating(AlarmManager.RTC, triggerAtMillis, intervalMillis, pendingIntent)
                Log.d("UpdateWidgetReceiver", "Widget update scheduled for the start of every hour at: ${calendar.time}");
                Log.d("UpdateWidgetReceiver", "Widget update scheduled for the start of every hour.");
            } catch (e: Exception) {
                Log.d("UpdateWidgetReceive", "cagaste.");
            }

        }
    }
}
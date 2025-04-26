package com.example.eduverse.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.eduverse.R
import com.example.eduverse.MainActivity
import androidx.core.net.toUri

/**
 * This class provides the functionality for the "Problem of the Day" widget.
 * It extends AppWidgetProvider to handle widget updates and interactions.
 */
class ProblemOfDayWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        mgr: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val prefs = context.getSharedPreferences("pod_prefs", Context.MODE_PRIVATE)
        val title = prefs.getString("pod_title", "No problem set") ?: ""

        appWidgetIds.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_problem_of_day).apply {
                setTextViewText(R.id.widget_problem_title, title)
                val intent = Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data   = "eduverse://problem_of_the_day".toUri()
                }
                val pending = PendingIntent.getActivity(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_root, pending)
            }
            mgr.updateAppWidget(id, views)
        }
    }
}

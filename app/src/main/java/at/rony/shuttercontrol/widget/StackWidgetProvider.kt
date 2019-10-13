package at.rony.shuttercontrol.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import at.rony.shuttercontrol.R
import at.rony.shuttercontrol.constants.Constants

/**
 * Main class for the widget, which loads the layout as remote views and uses the StackWidgetService
 * to load the shutter command views and displays them on the widget list.
 */

class StackWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (i in appWidgetIds.indices) {
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val rv = RemoteViews(context.packageName,
                R.layout.widget_main
            )

            rv.setRemoteAdapter(R.id.mainList, intent)

            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can settings a fillInIntent
            // to create unique before on an item to item basis.
            val toastIntent = Intent(context, StackWidgetProvider::class.java)
            toastIntent.action = Constants.SWITCH_INTENT_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setPendingIntentTemplate(R.id.mainList, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv)
        }
    }
}
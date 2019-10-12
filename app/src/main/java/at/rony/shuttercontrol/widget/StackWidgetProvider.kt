/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.rony.shuttercontrol.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import at.rony.shuttercontrol.activities.MainActivity
import at.rony.shuttercontrol.R
import at.rony.shuttercontrol.constants.Constants
import at.rony.shuttercontrol.tools.Logger

class StackWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        update(context,  appWidgetManager, appWidgetIds);
    }


    fun update(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // update each of the widgets with the remote adapter
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

            val activityIntent = Intent(context, MainActivity::class.java)
            activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            rv.setRemoteAdapter(R.id.mainList, intent)

            //rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent)

            //rv.setTextViewText(R.id.lastEvent, AppSettings.LAST_GCM_EVENT);
            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            //rv.setEmptyView(R.id.stack_view, R.id.empty_view)

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
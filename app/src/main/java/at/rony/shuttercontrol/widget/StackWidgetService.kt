package at.rony.shuttercontrol.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import at.rony.shuttercontrol.model.ShutterModel
import at.rony.shuttercontrol.R
import at.rony.shuttercontrol.constants.Constants
import at.rony.shuttercontrol.tools.CommandSettingsDB
import at.rony.shuttercontrol.tools.Logger

import java.util.ArrayList

/**
 * Loads the shutter command elements from the database and builds the remote views for the
 * StackWidgetProvider.
 */

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return StackRemoteViewsFactory(
            this.applicationContext
        )
    }
}

internal class StackRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    var listItems = ArrayList<ShutterModel>()
    lateinit var commandSettingsDB: CommandSettingsDB

    override fun onCreate() {
        commandSettingsDB = CommandSettingsDB(context)
        listItems = commandSettingsDB.getAllShutters
    }

    override fun onDestroy() {

        Logger.d("StackWidgetService", "onDestroy")
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        listItems.clear()
    }

    override fun getCount(): Int {
        return listItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        Logger.d("StackWidgetService", "getViewAt mWidgetItems:$listItems")

        val listItem = listItems[position]

        var rv = RemoteViews(context.packageName, R.layout.shutter_view)
        rv.setTextViewText(R.id.numberTextView, ""+listItem.windowName)

        var intent = Intent(Constants.BROADCAST_SEND_UDP_COMMAND)
        intent.putExtra(Constants.UDP_COMMAND, listItem.codeStop)
        rv.setOnClickFillInIntent(R.id.stopButton, intent)

        intent = Intent(Constants.BROADCAST_SEND_UDP_COMMAND)
        intent.putExtra(Constants.UDP_COMMAND, listItem.codeUp)
        rv.setOnClickFillInIntent(R.id.upButton, intent)

        intent = Intent(Constants.BROADCAST_SEND_UDP_COMMAND)
        intent.putExtra(Constants.UDP_COMMAND, listItem.codeDown)
        rv.setOnClickFillInIntent(R.id.downButton, intent)

        // Return the remote views object.
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        Logger.d("StackWidgetService", "getLoadingView")
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
        listItems = commandSettingsDB.getAllShutters
    }
}
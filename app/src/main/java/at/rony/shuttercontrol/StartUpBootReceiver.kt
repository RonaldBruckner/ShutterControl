package at.rony.shuttercontrol

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import at.rony.shuttercontrol.service.UdpService

/**
 * Starts the background service after boot, which is used to send UDP broadcasts
 */

class StartUpBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startService(Intent(context, UdpService::class.java))
    }
}
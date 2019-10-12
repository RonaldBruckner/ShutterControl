package at.rony.shuttercontrol.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import at.rony.shuttercontrol.constants.Constants.Companion.BROADCAST_SEND_UDP_COMMAND
import at.rony.shuttercontrol.constants.Constants.Companion.UDP_COMMAND
import at.rony.shuttercontrol.tools.UdpHandler
import at.rony.shuttercontrol.tools.UdpHandlerInterface
import at.rony.shuttercontrol.tools.Logger

class UdpService : Service(), UdpHandlerInterface {

    private val TAG = "UdpService"

    private lateinit var udpHandler: UdpHandler
    private lateinit var commandReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()

        Logger.d(TAG, "UdpService started")
        udpHandler = UdpHandler(this, this)

        commandReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context, intent: Intent) {

                Logger.d(TAG, "onReceive: "+intent.action)

                if (intent.action == BROADCAST_SEND_UDP_COMMAND) {
                    var udpCommand = intent.getStringExtra(UDP_COMMAND)
                    udpHandler.sendUdpBroadcast(udpCommand)
                }
            }
        }

        val broadcastFilter = IntentFilter()

        broadcastFilter.addAction(BROADCAST_SEND_UDP_COMMAND)
        this.registerReceiver(commandReceiver, broadcastFilter)
    }

    override fun onUdpCommandReceived(receivedCommand: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(commandReceiver)
    }
}

package at.rony.shuttercontrol.tools

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

import android.content.Context
import android.net.wifi.WifiManager
import at.rony.shuttercontrol.constants.Constants


/**
 * Receive and send base64 command strings as UDP broadcast.
 */

class UdpHandler(context: Context, udpHandlerInterface: UdpHandlerInterface) {

    private val TAG = UdpHandler::class.java.simpleName

    private lateinit var receivedCommand: String
    private var wifiManager: WifiManager
    private lateinit var serverSocket: DatagramSocket
    private var multicastLock: WifiManager.MulticastLock
    private var udpHandlerInterface: UdpHandlerInterface

    init {
        this.udpHandlerInterface = udpHandlerInterface
        wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifiManager.createMulticastLock(javaClass.simpleName)
    }

    /*
    Send a shutter command as base64 string as UDP packet.
    This is done in an own thread as it would block the UI thread.
     */
    fun sendUdpBroadcast(cmd: String) {

        Logger.d(TAG, "send: " + cmd)

        Thread(Runnable {
            try {
                    val s = DatagramSocket()
                    val local = InetAddress.getByName(Constants.BROADCAST_IP)
                    val message = cmd.toByteArray()
                    val p = DatagramPacket(message, message.size, local, Constants.UDP_PORT)
                    s.send(p)
            } catch (ex: Exception) {
                udpHandlerInterface.onUdpError()
                Logger.d(TAG, "send Exception: " + ex.message)
            }
        }).start()
    }

    /*
    Listen for a UDP broadcast to store a new shutter command.
    A multicastlock must be aquired so that WIFI brodcasts can be received.
    After a command has been received the lock gets released an the received command is handed over.
    This is done in an own thread as it would block the UI thread.
     */
    fun listenForUdpBroadcast() {
        Thread(Runnable {
            multicastLock.acquire()
            receivedCommand = read()
            multicastLock.release()

            Logger.d(TAG, "receivedCommand: " + receivedCommand)

            udpHandlerInterface.onUdpCommandReceived(receivedCommand)
        }).start()
    }

    /*
    Open a UDP serversocket to receive incoming packets and read them into a string
     */
    fun read(): String {
        Logger.d(TAG, "read UDP Socket")
        var command = ""
        try {
            val receiveData = ByteArray(2048)
            serverSocket = DatagramSocket(Constants.UDP_PORT)
            val receivePacket = DatagramPacket(receiveData, receiveData.size)
            serverSocket.receive(receivePacket)
            val lenght = receivePacket.length

            for (i in 0 until lenght) {
                command += receiveData[i].toChar()
            }

            serverSocket.close()

        } catch (e: Exception) {
            udpHandlerInterface.onUdpError()
            Logger.d(TAG, e.message)
        }
        return command
    }

    fun cancel() {
        try {
            serverSocket.close()
        } catch (e: Exception) {
            Logger.d(TAG, e.message)
        }
    }
}

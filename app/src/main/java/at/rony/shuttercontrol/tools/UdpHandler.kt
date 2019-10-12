package at.rony.shuttercontrol.tools

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Message
import android.util.Log

import at.rony.shuttercontrol.activities.MainActivity
import at.rony.shuttercontrol.constants.Constants

class UdpHandler(context: Context, udpHandlerInterface: UdpHandlerInterface) {

    private val TAG = UdpHandler::class.java.simpleName

    lateinit var receivedCommand: String
    var wifiManager: WifiManager
    lateinit var serverSocket: DatagramSocket
    var multicastLock: WifiManager.MulticastLock
    var udpHandlerInterface: UdpHandlerInterface

    init {
        this.udpHandlerInterface = udpHandlerInterface
        wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifiManager.createMulticastLock(javaClass.simpleName)
    }

    fun listenForUdpBroadcast() {
        Thread(Runnable {
            multicastLock.acquire()
            receivedCommand = read()
            multicastLock.release()

            Logger.d(TAG, "receivedCommand: " + receivedCommand)

            udpHandlerInterface.onUdpCommandReceived(receivedCommand)
        }).start()
    }

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

package at.rony.shuttercontrol.tools

interface UdpHandlerInterface {
    fun onUdpCommandReceived(receivedCommand: String)
    fun onUdpError() {}
}
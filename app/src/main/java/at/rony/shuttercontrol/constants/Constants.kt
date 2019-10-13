package at.rony.shuttercontrol.constants

class Constants {
    companion object {

        @kotlin.jvm.JvmField
        val BROADCAST_IP = "255.255.255.255"

        @kotlin.jvm.JvmField
        val UDP_PORT = 21000

        @kotlin.jvm.JvmField
        val MAX_COMMAND_INDEX_PER_SHUTTER = 2

        @kotlin.jvm.JvmField
        val SWITCH_INTENT_ACTION = "shutter.switch.action"

        @kotlin.jvm.JvmField
        val BROADCAST_SEND_UDP_COMMAND = "send.udp.command"

        @kotlin.jvm.JvmField
        val UDP_COMMAND = "udp.command"
    }
}
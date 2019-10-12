package at.rony.shuttercontrol.views

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.LinearLayout
import at.rony.shuttercontrol.model.ShutterModel
import at.rony.shuttercontrol.R
import at.rony.shuttercontrol.constants.Constants.Companion.BROADCAST_SEND_UDP_COMMAND
import at.rony.shuttercontrol.constants.Constants.Companion.UDP_COMMAND
import kotlinx.android.synthetic.main.shutter_view.view.*

class ShutterView(context: Context?) : LinearLayout(context) {

    lateinit var shutterModel: ShutterModel

    init {
        LayoutInflater.from(context).inflate(R.layout.shutter_view, this, true)

        downButton.setOnClickListener(OnClickListener {
            sendBroadcast(BROADCAST_SEND_UDP_COMMAND,shutterModel.codeUp)
        })

        stopButton.setOnClickListener(OnClickListener {
            sendBroadcast(BROADCAST_SEND_UDP_COMMAND,shutterModel.codeStop)
        })

        upButton.setOnClickListener(OnClickListener {
            sendBroadcast(BROADCAST_SEND_UDP_COMMAND,shutterModel.codeStop)
        })
    }

    fun sendBroadcast(command_type: String, command_content: String) {
        val intent = Intent(command_type)
        intent.putExtra(UDP_COMMAND, command_content)
        context.sendBroadcast(intent)
    }

    fun setButtonCodes(shutterModel: ShutterModel) {
        this.shutterModel = shutterModel
        numberTextView.setText(""+shutterModel.windowName)
    }

}
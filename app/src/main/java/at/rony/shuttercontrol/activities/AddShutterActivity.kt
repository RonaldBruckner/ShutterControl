package at.rony.shuttercontrol.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_shutter.*
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager

import at.rony.shuttercontrol.R
import java.util.ArrayList
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import at.rony.shuttercontrol.constants.Constants.Companion.MAX_COMMAND_INDEX_PER_SHUTTER
import at.rony.shuttercontrol.tools.*
import at.rony.shuttercontrol.widget.StackWidgetProvider

/**
 * This Activity is used to add new shutter control elements.
 * Each element consists of three commands, up, stop and down.
 * These commands must be learned, the user has to press the according buttons on the RF or IR
 * control so that a UDP broadcast is send out.
 * First a name for the new element must be entered, the the udpHandler is used to listen for
 * UDP-broadcasts. If a UDP packet is received the user gets instructed to press the next button.
 * After all commands have been received, the new control element gets stored and the activity will
 * be finished.
 */

class AddShutterActivity : AppCompatActivity(), UdpHandlerInterface {

    private val TAG = AddShutterActivity::class.java.simpleName

    lateinit var inputMethodManager: InputMethodManager
    lateinit var udpHandler: UdpHandler
    lateinit var commandSettingsDB: CommandSettingsDB

    var commandIndex = 0
    val commandList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_add_shutter)

        commandSettingsDB = CommandSettingsDB(this)
        udpHandler = UdpHandler(this, this)

        windowNameEditText.requestFocus()
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        windowNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                if(p0.length>0) {   //only display the learnButton if a name has been entered
                    learnButton.visibility= View.VISIBLE
                } else {
                    learnButton.visibility= View.GONE
                }
            }
        })

        learnButton.setOnClickListener(View.OnClickListener {
            hideKeyboard()
            startLayout.visibility = View.GONE
            learnLayout.visibility = View.VISIBLE
            udpHandler.listenForUdpBroadcast()
        })

        cancelButton.setOnClickListener(View.OnClickListener {
            startLayout.visibility = View.VISIBLE
            learnLayout.visibility = View.GONE
            udpHandler.cancel()
        })
    }

    fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(windowNameEditText.getWindowToken(),
            InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }



    override fun onUdpCommandReceived(receivedCommand: String) {
        Logger.d(TAG, "onUdpCommandReceived: "+receivedCommand)

         /* udphandler was listening for a new command on its own thread.
         *  switch to ui thread and start lsieting for the next command,
         *  or store the received commands, update widget and go back to main page
         */
        runOnUiThread {
            commandList.add(receivedCommand)
            if(commandIndex < MAX_COMMAND_INDEX_PER_SHUTTER) {
                commandIndex++
                val id = getResources().getIdentifier("learn_command_text_"+commandIndex, "string",getPackageName())
                learnDescriptionTextView.text = resources.getString(id)
                udpHandler.listenForUdpBroadcast()

            } else {
                commandSettingsDB.addWindow(windowNameEditText.text.toString(), commandList[0],commandList[1],commandList[2])

                //update widget, so that new shutter gets displayed
                val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
                    ComponentName(
                        application, StackWidgetProvider::class.java
                    )
                )
                val shutterWidget = StackWidgetProvider()
                shutterWidget.onUpdate(this, AppWidgetManager.getInstance(this), ids)

                finish()
            }
        }
    }

    override fun onUdpError() {
        runOnUiThread(Runnable {
            Utils.showAlertDialog(this@AddShutterActivity, resources.getString(R.string.error), resources.getString(R.string.add_shutter_error_text))
        })
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }
}

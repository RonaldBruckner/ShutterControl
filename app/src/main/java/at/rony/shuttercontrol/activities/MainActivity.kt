package at.rony.shuttercontrol.activities

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle

import kotlinx.android.synthetic.main.content_main.*

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import at.rony.shuttercontrol.service.UdpService
import at.rony.shuttercontrol.R
import at.rony.shuttercontrol.adapters.DeviceAdapter
import at.rony.shuttercontrol.tools.CommandSettingsDB
import at.rony.shuttercontrol.tools.Utils
import at.rony.shuttercontrol.widget.StackWidgetProvider
import com.baoyz.swipemenulistview.SwipeMenu
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuItem
import kotlinx.android.synthetic.main.activity_main.*

/**
 * This Activity displays all the stored shutter control elements in a list.
 * Elements can be removed by swiping left, new elements can be added with the floating button.
 */

class MainActivity : AppCompatActivity() {

    lateinit var adapter: DeviceAdapter
    lateinit var commandSettingsDB: CommandSettingsDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)

        /*start background service in case the app was just installed and
          the service was not started by boor broadcast receiver
         */
        if(!Utils.isUdpServiceRunning(this)) {
            var maintenanceService = Intent(this, UdpService::class.java)
            startService(maintenanceService);
        }

        adapter = DeviceAdapter(this)
        mainList.adapter = adapter

        val creator = object : SwipeMenuCreator {
            override fun create(menu: SwipeMenu) {
                // create "delete" item
                val deleteItem = SwipeMenuItem(
                    applicationContext
                )
                // set item width
                deleteItem.setWidth(Utils.dp2px(this@MainActivity,90))
                // set a icon
                deleteItem.setIcon(android.R.drawable.ic_delete)
                // add to menu
                menu.addMenuItem(deleteItem)
            }
        }

        // set creator
        mainList.setMenuCreator(creator)

        mainList.setOnMenuItemClickListener { position, _, index ->
            when (index) {
                0 -> {
                   var shutterToDelete = adapter.getItem(position);
                    commandSettingsDB.deleteWindow(shutterToDelete.windowName)
                    adapter.setItems(commandSettingsDB.getAllShutters)

                    //update widget, so that shutter gets removed
                    val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
                        ComponentName(
                            application, StackWidgetProvider::class.java
                        )
                    )
                    val shutterWidget = StackWidgetProvider()
                    shutterWidget.onUpdate(this, AppWidgetManager.getInstance(this), ids)
                }
            }// open
            // delete
            // false : close the menu; true : not close the menu
            false
        }

        addShutterControlButton.setOnClickListener {
            if(Utils.isWifiConnected(this@MainActivity)) { //WIFI must be connected to add a new shutter control element
                startActivity(Intent(this, AddShutterActivity::class.java))
            } else {
                Utils.showAlertDialog(this@MainActivity, resources.getString(R.string.no_wifi), resources.getString(R.string.no_wifi_text));
            }
        }

        commandSettingsDB = CommandSettingsDB(this)
    }

    //load all shutter items  and display in list
    override fun onResume() {
        super.onResume()
        adapter.setItems(commandSettingsDB.getAllShutters)
    }
}

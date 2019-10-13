package at.rony.shuttercontrol.tools

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import at.rony.shuttercontrol.service.UdpService
import android.util.TypedValue
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AlertDialog


object Utils {

    private val TAG = Utils::class.java.simpleName

    /*
    Check if the background service is running
     */
    fun isUdpServiceRunning(context: Context): Boolean {
        try {
            val manager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
            val serviceName = UdpService::class.java.canonicalName

            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName == service.service.className) {
                    return true
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "isUdpServiceRunning Exception:" + e.message)
        }

        return false
    }

    /*
    Convert dp values to px
     */
    fun dp2px(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            context.getResources().getDisplayMetrics()
        ).toInt()
    }

    fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return cm.activeNetworkInfo.isConnected
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        return cm.activeNetworkInfo.isConnected
                    }
                }
            }
        }
        return false
    }

    fun showAlertDialog(context: Context, title: String, message: String) {
        val dialog = AlertDialog.Builder(context)
        dialog.setMessage(message)
        dialog.setTitle(title)
        dialog.setNeutralButton("OK",
            DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })
        val alertDialog = dialog.create()
        alertDialog.show()
    }
}

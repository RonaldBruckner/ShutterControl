package at.rony.shuttercontrol.tools

import android.util.Log

import at.rony.shuttercontrol.constants.AppSettings

object Logger {

    fun d(tag: String, text: String?) {
        if (AppSettings.DEBUG) {
            Log.d(tag, text)
        }
    }

    fun i(tag: String, text: String?) {
        if (AppSettings.DEBUG) {
            Log.i(tag, text)
        }
    }

    fun e(tag: String, text: String?) {
        if (AppSettings.DEBUG) {
            Log.e(tag, text)
        }
    }
}

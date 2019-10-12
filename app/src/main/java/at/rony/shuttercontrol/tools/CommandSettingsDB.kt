package at.rony.shuttercontrol.tools

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import at.rony.shuttercontrol.constants.AppSettings.Companion.DATABASE_VERSION
import at.rony.shuttercontrol.model.ShutterModel
import java.util.ArrayList

class CommandSettingsDB
/**
 * Constructor
 * Takes and keeps a reference of the passed context inAnimation order to access to the application assets and dbCursorources.
 * @param context
 */
(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val TAG = CommandSettingsDB::class.java.simpleName
        val DATABASE_NAME = "ShutterControl.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table settings " + "(id integer primary key, window_name text,command_up text,command_stop text ,command_down text)")
        Logger.e(TAG, "onCreate")
    }

    fun addWindow(window_name: String, command_up: String, command_stop: String, command_down: String) {
        Logger.d(TAG, "addWindow window_name:" +window_name)
        val db = this.writableDatabase

        try {
            val contentValues = ContentValues()
            contentValues.put("window_name", window_name)
            contentValues.put("command_up", command_up)
            contentValues.put("command_stop", command_stop)
            contentValues.put("command_down", command_down)

            db.insert("settings", null, contentValues)
        } catch (e: Exception) {
            Logger.e(TAG, "addWindow Exception:" + e.message)
        } finally {
            db.close()
        }
    }

    val getAllShutters: ArrayList<ShutterModel>
        get() {
            var dbCursor: Cursor? = null
            var readableDB = this.readableDatabase
            val windowList = ArrayList<ShutterModel>()

            try {
                dbCursor = readableDB.rawQuery("select * from settings", null)
                dbCursor.moveToFirst()

                while (dbCursor.isAfterLast == false) {

                    val itemFolder = ShutterModel(
                        dbCursor.getString(dbCursor.getColumnIndex("window_name")),
                        dbCursor.getString(dbCursor.getColumnIndex("command_up")),
                        dbCursor.getString(dbCursor.getColumnIndex("command_stop")),
                        dbCursor.getString(dbCursor.getColumnIndex("command_down"))
                    )
                    windowList.add(itemFolder)

                    dbCursor.moveToNext()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (dbCursor != null && !dbCursor.isClosed) {
                    dbCursor.close()
                }
            }

            Logger.d(TAG, "getAllShutters: "+windowList.size)

            return windowList
        }

    fun deleteWindow(windowName: String ) {
        Logger.d(TAG, "deleteWindow: "+windowName)
        val db = this.writableDatabase
        try {
            db.delete("settings", "window_name=?", arrayOf(windowName))
        } catch (e: Exception) {
            Logger.e(TAG, "deleteWindow Exception:" + e.message)
        } finally {
            db.close()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Logger.e(TAG, "onUpgrade")
        onCreate(db)
    }
}

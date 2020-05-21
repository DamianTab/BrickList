package damian.tab.bricklist

import android.content.Context
import android.content.SharedPreferences

// Main
const val REQUEST_CODE = 1000
// Settings
const val SETTINGS_NAME = "brickList_settings_data"
const val SETTINGS_MODE = Context.MODE_PRIVATE
const val DATABASE_URL_FIELD = "database_url"
const val DATABASE_VERSION_FIELD = "database_versions"
const val SHOW_ARCHIVED_FIELD = "show_archived"
// Database
const val DEFAULT_DATABASE_URL = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
const val ASSETS_PATH = "databases"
const val DATABASE_NAME = "BrickList.db"
const val DATABASE_VERSION = 1

inline fun SharedPreferences.apply(action: SharedPreferences.Editor.() -> Unit){
    val editor = this.edit()
    action.invoke(editor)
    editor.apply()
}
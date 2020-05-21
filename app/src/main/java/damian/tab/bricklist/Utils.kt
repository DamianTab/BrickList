package damian.tab.bricklist

import android.content.Context
import android.content.SharedPreferences


const val REQUEST_CODE = 1000
const val SETTINGS_NAME = "Settings_Data"
const val SETTINGS_MODE = Context.MODE_PRIVATE
const val DATABASE_URL_FIELD = "database_url"
const val SHOW_ARCHIVED_FIELD = "show_archived"

inline fun SharedPreferences.apply(action: SharedPreferences.Editor.() -> Unit){
    val editor = this.edit()
    action.invoke(editor)
    editor.apply()
}
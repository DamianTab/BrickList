package damian.tab.bricklist

import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    override fun finish() {
        val sharedPreferences = getSharedPreferences(SETTINGS_NAME, SETTINGS_MODE)
        sharedPreferences.apply { putString(DATABASE_URL_FIELD, url_input.text.toString()) }
        sharedPreferences.apply { putBoolean(SHOW_ARCHIVED_FIELD, archived_switch.isChecked) }
        setResult(Activity.RESULT_OK,intent)
        super.finish()
    }
}

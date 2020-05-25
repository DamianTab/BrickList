package damian.tab.bricklist

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import damian.tab.bricklist.task.AddNewProjectTask
import kotlinx.android.synthetic.main.activity_new_inventory.*
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class NewInventoryActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_inventory)
        sharedPreferences = getSharedPreferences(SETTINGS_NAME, SETTINGS_MODE)

        new_inventory_check_button.setOnClickListener {
            checkCorrectness()
        }

        new_inventory_add_button.setOnClickListener {
            addNewProject()
        }

    }

    override fun finish() {
        setResult(Activity.RESULT_OK,intent)
        super.finish()
    }

    private fun checkCorrectness() {
        val id = new_inventory_id.text.toString()
        val url =
            sharedPreferences.getString(DATABASE_URL_FIELD, DEFAULT_DATABASE_URL) + id + ".xml";

        println(url)
        val task = AddNewProjectTask(this)
        task.execute(url)
    }

    private fun addNewProject() {
        val projectName = new_inventory_name.text.toString()
        val id = new_inventory_id.text.toString()
        val url =
            sharedPreferences.getString(DATABASE_URL_FIELD, DEFAULT_DATABASE_URL) + id + ".xml";
        val task = AddNewProjectTask(this)
        task.execute(url, projectName)
    }
}

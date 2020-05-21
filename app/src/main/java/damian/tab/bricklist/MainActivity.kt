package damian.tab.bricklist

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import damian.tab.bricklist.database.SQLExecutor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        SQLExecutor.initialize(this)
        sharedPreferences = getSharedPreferences(SETTINGS_NAME, SETTINGS_MODE)

        add_button.setOnClickListener {
            startActivityForResult(Intent(this, NewInventoryActivity::class.java), REQUEST_CODE)
        }
        updateList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivityForResult(Intent(this, SettingsActivity::class.java), REQUEST_CODE)
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) updateList()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateList() {
        val showArchived = sharedPreferences.getBoolean(SHOW_ARCHIVED_FIELD, DEFAULT_ARCHIVED_VALUE)
        val inventories = SQLExecutor.getInventories(showArchived)
        projects.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, inventories.map { it.name })
        projects.setOnItemClickListener { _, _, position, _ -> listViewOnClick(position); }
    }

    private fun listViewOnClick(position: Int) {
        println(position)
//        var intent = Intent(this,ProjectActivity::class.java);
//        startActivity(intent)
    }
}

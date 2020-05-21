package damian.tab.bricklist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import damian.tab.bricklist.database.SQLExecutor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        SQLExecutor.initialize(this)

        add_button.setOnClickListener {
//            startActivityForResult(Intent(this, NewInventoryActivity::class.java), REQUEST_CODE)
            SQLExecutor.getInventories(false)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivityForResult(Intent(this, SettingsActivity::class.java), REQUEST_CODE)
        return super.onOptionsItemSelected(item)
    }

    private fun updateList(){
//        val inv = database.getInventoryNames(ACTIVE_ONLY);
//        val show = ArrayList<String>();
//        for(x in inv)
//        {
//            show.add(x.name.toString())
//        }
//        projects.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,show)
//        projects.setOnItemClickListener{_, _, position, _ ->  listviewOnClick(position);}
    }

    private fun listviewOnClick(position:Int)
    {
//        var intent = Intent(this,ProjectActivity::class.java);
//        startActivity(intent)
    }
}

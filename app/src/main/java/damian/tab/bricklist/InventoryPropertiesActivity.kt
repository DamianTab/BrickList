package damian.tab.bricklist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import damian.tab.bricklist.domain.Inventory

class InventoryPropertiesActivity : AppCompatActivity() {

    private var inventory: Inventory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_properties)
        inventory = intent.extras?.get(INVENTORY_DATA) as Inventory?
        val menuBar = supportActionBar
        menuBar!!.title = inventory!!.name
        menuBar.subtitle = "Project Name"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.inventory_properties_menu, menu)
        val menuItem = menu!!.findItem(R.id.properties_archived_switch).actionView
        val switch = menuItem.findViewById<Switch>(R.id.properties_archived_switch_supplier)
        switch.isChecked = inventory!!.active == 0
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.properties_archived_switch -> {
            }

            R.id.properties_save_button -> {

            }
            R.id.properties_export_button -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}

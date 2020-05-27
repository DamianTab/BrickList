package damian.tab.bricklist

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import damian.tab.bricklist.adapter.InventoryPartListAdapter
import damian.tab.bricklist.database.SQLExecutor
import damian.tab.bricklist.domain.Inventory
import damian.tab.bricklist.domain.InventoryPart
import kotlinx.android.synthetic.main.activity_inventory_properties.*

class InventoryPropertiesActivity : AppCompatActivity() {

    private lateinit var inventory: Inventory
    private lateinit var inventoryParts: List<InventoryPart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_properties)
        SQLExecutor.initialize(this)
        inventory = (intent.extras?.get(INVENTORY_DATA) as Inventory?)!!
        val menuBar = supportActionBar
        menuBar!!.title = inventory.name
        menuBar.subtitle = "Project Name"

        println(inventory.lastAccess)

        //todo by nie sciagac na nowo tych czesci tylko je zapisywac w javie
        inventoryParts = SQLExecutor.getInventoryParts(inventory.id)
        SQLExecutor.supplyPartsNames(inventoryParts)
        SQLExecutor.supplyPartsColors(inventoryParts)
        SQLExecutor.supplyDesignCodesAndImages(inventoryParts)
        inventoryParts.filter {
            it.itemId == -1
        }.forEach {
            //todo naprawic partCode - obecnie jest null
            Toast.makeText(
                this,
                "There is no information about brick with ItemCode: ${it.partCode} and Color: ${it.color}",
                Toast.LENGTH_LONG
            ).show()
        }

        inventoryParts = inventoryParts.filter {
            it.itemId != -1
        }

        inventory_part_list.adapter = InventoryPartListAdapter(this, inventoryParts)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.inventory_properties_menu, menu)
        val menuItemView = menu!!.findItem(R.id.properties_archived_switch).actionView
        val switch = menuItemView.findViewById<Switch>(R.id.properties_archived_switch_supplier)
        switch.isChecked = inventory.active == 0
        switch.setOnClickListener {
            inventory.active = if (switch.isChecked) 0 else 1
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.properties_save_button -> {
                save()
            }
            R.id.properties_export_button -> {
                exportToXML()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }

    private fun save() {
        SQLExecutor.updateInventoryStatusAndAccessDate(inventory)
        inventoryParts.forEach {
            SQLExecutor.updateInventoryPart(it)
        }
    }

    private fun exportToXML() {
        //todo eksport do XML
        println("EXPORT")

    }
}

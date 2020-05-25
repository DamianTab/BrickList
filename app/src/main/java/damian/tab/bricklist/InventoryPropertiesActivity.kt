package damian.tab.bricklist

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import damian.tab.bricklist.adapter.InventoryPartListAdapter
import damian.tab.bricklist.database.SQLExecutor
import damian.tab.bricklist.domain.Inventory
import kotlinx.android.synthetic.main.activity_inventory_properties.*
import java.io.FileNotFoundException
import java.net.URL

class InventoryPropertiesActivity : AppCompatActivity() {

    private lateinit var inventory: Inventory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_properties)
        SQLExecutor.initialize(this)
        inventory = (intent.extras?.get(INVENTORY_DATA) as Inventory?)!!
        val menuBar = supportActionBar
        menuBar!!.title = inventory.name
        menuBar.subtitle = "Project Name"

        val inventoryParts = SQLExecutor.getInventoryParts(inventory.id)
        SQLExecutor.supplyPartsNames(inventoryParts)
        SQLExecutor.supplyPartsColors(inventoryParts)
        SQLExecutor.supplyCodesAndImages(inventoryParts)

        inventoryParts.forEach { part ->
            if (part.image == null && part.code != null) {
                var url = URL("https://www.lego.com/service/bricks/5/2/" + part.code)
                try {
                    url.openConnection().getInputStream().use {
                        part.image = BitmapFactory.decodeStream(it)
                    }
                    //todo zapisanie zdjecia do bazy danych

                } catch (e: FileNotFoundException) {
                    url =
                        if (part.colorId == -1) URL("https://www.bricklink.com/PL/" + part.code + ".jpg") else URL(
                            "http://img.bricklink.com/P/" + part.colorId + "/" + part.code + ".jpg"
                        )
                    try {
                        url.openConnection().getInputStream().use {
                            part.image = BitmapFactory.decodeStream(it)
                        }
                        //todo zapisanie zdjecia do bazy danych

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        inventory_part_list.adapter = InventoryPartListAdapter(this, inventoryParts)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.inventory_properties_menu, menu)
        val menuItemView = menu!!.findItem(R.id.properties_archived_switch).actionView
        val switch = menuItemView.findViewById<Switch>(R.id.properties_archived_switch_supplier)
        switch.isChecked = inventory.active == 0
        switch.setOnClickListener {
            SQLExecutor.updateInventoryStatus(inventory.id, switch.isChecked)
        }
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun save() {
        SQLExecutor.updateInventoryDate(inventory.id)
        println("SAVE")
    }

    private fun exportToXML() {
        println("EXPORT")

    }
}

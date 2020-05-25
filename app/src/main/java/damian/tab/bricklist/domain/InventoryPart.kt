package damian.tab.bricklist.domain

import android.database.Cursor
import android.graphics.Bitmap

class InventoryPart : SQLParser {

    //    Database
    var id: Int = -1
    var typeId: Int = -1
    var itemId: Int = -1
    var quantityInSet: Int = -1
    var quantityInStore: Int = -1
    var colorId: Int = -1
    var extra: Int = -1

    //    Extra
    var inventoryId: Int? = null
    var designId: Int? = null
    var name: String? = null
    var color: String? = null
    var image: Bitmap? = null


    override fun parse(cursor: Cursor): InventoryPart {
        this.id = cursor.getInt(0)
        this.typeId = cursor.getInt(1)
        this.itemId = cursor.getInt(2)
        this.quantityInSet = cursor.getInt(3)
        this.quantityInStore = cursor.getInt(4)
        this.colorId = cursor.getInt(5)
        this.extra = cursor.getInt(6)
        return this
    }
}
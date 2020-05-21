package damian.tab.bricklist.domain

import android.database.Cursor

class InventoryPart : SQLParser {

    var id: Int = -1
    var typeId: Int = -1
    var itemId: Int = -1
    var quantityInSet: Int = -1
    var quantityInStore: Int = -1
    var colorId: Int = -1
    var extra: Int = -1

    override fun parse(cursor: Cursor): InventoryPart {

        this.id = cursor.getInt(0)
        this.typeId = cursor.getInt(2)
        this.itemId = cursor.getInt(3)
        this.quantityInSet = cursor.getInt(4)
        this.quantityInStore = cursor.getInt(5)
        this.colorId = cursor.getInt(6)
        this.extra = cursor.getInt(7)
        return this
    }
}
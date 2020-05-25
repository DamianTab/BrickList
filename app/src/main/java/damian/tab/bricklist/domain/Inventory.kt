package damian.tab.bricklist.domain

import android.database.Cursor
import java.io.Serializable

class Inventory : SQLParser, Serializable {

    var id: Int = -1
    var name: String = ""
    var active: Int = 1
    var lastActivity: Int = 0

    //todo dodac liste inventoryPart - jako optymalizacje - by nie pobierało za każdym razem

    constructor()
    constructor(id: Int, name: String, active: Int, date: Int) {
        this.id = id
        this.name = name
        this.active = active
        this.lastActivity = date
    }


    override fun parse(cursor: Cursor): Inventory {
        id = cursor.getInt(0)
        name = cursor.getString(1)
        active = cursor.getInt(2)
        lastActivity = cursor.getInt(3)
        return this
    }

}
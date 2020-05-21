package damian.tab.bricklist.database

import android.content.Context
import damian.tab.bricklist.domain.Inventory

object SQLExecutor {

    private var databaseManager: DatabaseManager? = null

    fun initialize(context: Context){
        databaseManager = DatabaseManager(context)
    }

    fun getInventories(showArchived: Boolean = false): List<Inventory> {
        val inventories = ArrayList<Inventory>()
        println("---------------------------------")
        val myDatabase = databaseManager!!.readableDatabase
        var query = "SELECT * FROM Inventories"
        query += if (showArchived) ";" else  " WHERE ACTIVE=1;"
        val cursor = myDatabase.rawQuery(query, null)
        while(cursor.moveToNext()){
            val inventory = Inventory().parse(cursor)
            inventories.add(inventory)
        }
        println("---------------------------------")
        return inventories
    }
}
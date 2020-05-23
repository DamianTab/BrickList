package damian.tab.bricklist.database

import android.content.Context
import damian.tab.bricklist.domain.Inventory

object SQLExecutor {

    private lateinit var databaseManager: DatabaseManager

    fun initialize(context: Context) {
        databaseManager = DatabaseManager(context)
    }

    fun getInventories(showArchived: Boolean = false): List<Inventory> {
        val inventories = ArrayList<Inventory>()
        var query = "SELECT * FROM Inventories"
        query += if (showArchived) ";" else " WHERE ACTIVE=1;"
        val database = databaseManager.readableDatabase
        val cursor = database.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val inventory = Inventory().parse(cursor)
            inventories.add(inventory)
        }
        return inventories
    }

    fun updateInventoryStatus(id: Int, isArchived: Boolean) {
        println("--------------------------------- Update status")
        val value = if (isArchived) 0 else 1
        val query = "update Inventories set Active=$value where id=$id;"
        execWritableQuery(query)
    }

    private fun execWritableQuery(query: String) {
        val database = databaseManager.writableDatabase
        database.beginTransaction()
        database.execSQL(query)
        database.setTransactionSuccessful()
        database.endTransaction()
    }
}
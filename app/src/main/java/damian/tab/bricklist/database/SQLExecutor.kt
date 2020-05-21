package damian.tab.bricklist.database

import android.content.Context

class SQLExecutor(private val context: Context) {

    private val databaseManager = DatabaseManager(context)

    fun getInventories(showInactive: Boolean = false){
        val myDatabase = databaseManager.readableDatabase
        val cursor = myDatabase.rawQuery("SELECT * FROM Inventories;", null)
        while(cursor.moveToNext()){
            var id= cursor.getInt(0)
            val name = cursor.getString(1)
            val ac = cursor.getInt(2)
            val la = cursor.getInt(3)
            println(name)
        }
        println("ZAKONCZYLOOO")
    }
}
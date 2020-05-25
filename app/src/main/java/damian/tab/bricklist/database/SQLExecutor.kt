package damian.tab.bricklist.database

import android.content.ContentValues
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import damian.tab.bricklist.Factory
import damian.tab.bricklist.domain.Inventory
import damian.tab.bricklist.domain.InventoryPart
import damian.tab.bricklist.domain.SQLParser
import damian.tab.bricklist.getTodayDate
import org.w3c.dom.NodeList

object SQLExecutor {

    private lateinit var databaseManager: DatabaseManager

    fun initialize(context: Context) {
        databaseManager = DatabaseManager(context)
    }

//    Create Inventory

    fun checkIfProjectExists(name: String?): Boolean {
        val query = "SELECT COUNT(*) FROM Inventories WHERE Name LIKE \"$name\""
        val db = databaseManager.readableDatabase
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        val result = cursor.getInt(0)
        cursor.close()
        return result != 0
    }

    fun addProject(inventory: Inventory) {
        val database = databaseManager.writableDatabase
        val values = ContentValues()
        values.put("id", inventory.id)
        values.put("Name", inventory.name)
        values.put("Active", inventory.active)
        values.put("LastAccessed", inventory.lastActivity)
        database.insert("Inventories", null, values)
    }

    fun addInventoryPart(attributes: NodeList, inventory: Inventory) {
        val database = databaseManager.writableDatabase
        val values = ContentValues()
        val typeId = getTypeId(attributes.item(1).textContent.toString().trim())
        values.put("InventoryID", inventory.id)
        values.put("TypeID", typeId)
        values.put("ItemID", attributes.item(3).textContent.toString().trim())
        values.put("QuantityInSet", Integer.parseInt(attributes.item(5).textContent.toString()))
        values.put("ColorID", Integer.parseInt(attributes.item(7).textContent.toString()))
        database.insert("InventoriesParts", null, values)
    }

    fun getLastInventoryId(): Int {
        val query = "select max(id) from Inventories;"
        val database = databaseManager.readableDatabase
        val cursor = database.rawQuery(query, null)
        var lastId = -1
        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(0)
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return lastId
    }

    private fun getTypeId(typeCode: String) : Int {
        val database = databaseManager.writableDatabase
        val query = "SELECT id FROM ItemTypes WHERE code LIKE \"$typeCode\""
        val cursor = database.rawQuery(query, null)
        cursor.moveToFirst()
        val toReturn = Integer.parseInt(cursor.getString(0))
        cursor.close()
        return toReturn
    }


//    Inventory -------------------------------------------------

    fun getInventories(showArchived: Boolean = false): List<Inventory> {
        val inventories = ArrayList<SQLParser>()
        var query = "SELECT * FROM Inventories"
        query += if (showArchived) ";" else " WHERE ACTIVE=1;"
        execReadableQuery(query, inventories, Inventory::class.java)
        return inventories as ArrayList<Inventory>
    }

    fun updateInventoryStatus(id: Int, isArchived: Boolean) {
        println("--------------------------------- Update status")
        val value = if (isArchived) 0 else 1
        val query = "update Inventories set Active=$value where id=$id;"
        execWritableQuery(query)
    }

//    Inventory parts -------------------------------------------------

    //todo sprawdzic czy na pewno działa
    fun getInventoryParts(inventoryId: Int): List<InventoryPart> {
        val inventoryParts = ArrayList<SQLParser>()
        val query =
            "select id, TypeID, ItemID, QuantityInSet, QuantityInStore, ColorID, extra from InventoriesParts where InventoryID = $inventoryId"
        return execReadableQuery(
            query,
            inventoryParts,
            InventoryPart::class.java
        ) as ArrayList<InventoryPart>
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateInventoryDate(inventoryId: Int) {
        val query =
            "update Inventories set LastAccessed=" + getTodayDate() + " where _id=" + inventoryId + ";"
        execWritableQuery(query)
    }

//        todo trzeba dodawac jeszcze id inventory

//    fun getItemsIds(parts: ArrayList<InventoryPart>): ArrayList<InventoryPart>{
//        parts.forEach {
//            val query = "select _id from Parts where Code=\"${it.code}\""
//            val db = this.readableDatabase
//            val cursor = db.rawQuery(query, null)
//            if(cursor.moveToFirst()) {
//                it.itemId = cursor.getInt(0)
//            } else {
//                it.itemId = -9
//            }
//            if (cursor != null && !cursor.isClosed) {
//                cursor.close()
//            }
//        }
//        return parts
//    }
//
//    fun getItemsColors(parts: ArrayList<InventoryPart>): ArrayList<InventoryPart>{
//        parts.forEach {
//            val query = "select Name from Colors where Code=\"${it.colorCode}\""
//            val db = this.readableDatabase
//            val cursor = db.rawQuery(query, null)
//            if(cursor.moveToFirst()) {
//                it.color = cursor.getString(0)
//            }
//            if (cursor != null && !cursor.isClosed) {
//                cursor.close()
//            }
//        }
//        return parts
//    }
//
//    fun getItemImage(part: InventoryPart): InventoryPart {
//        val query = "select Image from Codes where Code=" + part.designId + ";"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, null)
//        val blob: ByteArray?
//        if (cursor.moveToFirst()) {
//            blob = cursor.getBlob(0)
//            if (blob != null) {
//                part.image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
//            }
//        }
//        if (cursor != null && !cursor.isClosed) {
//            cursor.close()
//        }
//        return part
//    }
//
//    private fun checkIfDesignIDExists(color: Int, itemId: Int): Boolean {
//        val db = this.readableDatabase
//        val query = "select Code from Codes where ColorID=$color and ItemID=$itemId"
//        val cursor = db.rawQuery(query, null)
//        if (cursor.count <= 0) {
//            cursor.close()
//            return false
//        }
//        if (cursor != null && !cursor.isClosed) {
//            cursor.close()
//        }
//        return true
//    }
//
//    fun getItemsDesignIds(parts: ArrayList<InventoryPart>): ArrayList<InventoryPart>{
//        parts.forEach {
//            if (checkIfDesignIDExists(it.colorCode!!, it.itemId!!)) {
//                val query = "select Code from Codes where ColorID=${it.colorCode} and ItemID=${it.itemId}"
//                val db = this.readableDatabase
//                val cursor = db.rawQuery(query, null)
//                if(cursor.moveToFirst()) {
//                    it.designId = cursor.getInt(0)
//                }
//                if (cursor != null && !cursor.isClosed) {
//                    cursor.close()
//                }
//            }
//        }
//        return parts
//    }
//
//    fun getItemsNames(parts: ArrayList<InventoryPart>): ArrayList<InventoryPart>{
//        parts.forEach {
//            val query = "select Name from Parts where _id=${it.itemId}"
//            val db = this.readableDatabase
//            val cursor = db.rawQuery(query, null)
//            if(cursor.moveToFirst()) {
//                it.name = cursor.getString(0)
//            }
//            if (cursor != null && !cursor.isClosed) {
//                cursor.close()
//            }
//        }
//        return parts
//    }
//
//    fun updateQuantityInStore(inventoryId: String, parts: ArrayList<InventoryPart>){
//        parts.forEach {
//            val db = this.writableDatabase
//            db.beginTransaction()
//            val query = "update InventoriesParts set QuantityInStore=" + it.quantityInStore + " where InventoryID=" + inventoryId + " and _id=" + it.id+ ";"
//            writableDatabase.execSQL(query)
//            writableDatabase.setTransactionSuccessful()
//            writableDatabase.endTransaction()
//        }
//    }


    private fun <T : SQLParser> execReadableQuery(
        query: String,
        resultList: ArrayList<SQLParser>,
        type: Class<T>
    ): ArrayList<SQLParser> {
        val database = databaseManager.readableDatabase
        val cursor = database.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val instance = Factory(type).newInstance()
            resultList.add(instance.parse(cursor))
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return resultList
    }

    private fun execWritableQuery(query: String) {
        val database = databaseManager.writableDatabase
        database.beginTransaction()
        database.execSQL(query)
        database.setTransactionSuccessful()
        database.endTransaction()
    }
}
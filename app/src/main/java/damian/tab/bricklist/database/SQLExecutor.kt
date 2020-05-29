package damian.tab.bricklist.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import damian.tab.bricklist.Factory
import damian.tab.bricklist.domain.Inventory
import damian.tab.bricklist.domain.InventoryPart
import damian.tab.bricklist.domain.SQLParser
import damian.tab.bricklist.getTodayDate
import java.io.ByteArrayOutputStream

object SQLExecutor {

    private lateinit var databaseManager: DatabaseManager

    fun initialize(context: Context) {
        databaseManager = DatabaseManager(context)
    }

//    New Inventory Activity -------------------------------------------------

    fun checkIfInventoryExists(name: String?): Boolean {
        val query = "SELECT COUNT(*) FROM Inventories WHERE Name LIKE \"$name\""
        val db = databaseManager.readableDatabase
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        val result = cursor.getInt(0)
        closeCursor(cursor)
        return result != 0
    }

    fun addNewInventory(inventory: Inventory) {
        val database = databaseManager.writableDatabase
        val values = ContentValues()
        values.put("id", inventory.id)
        values.put("Name", inventory.name)
        values.put("Active", inventory.active)
        values.put("LastAccessed", inventory.lastAccess)
        database.insert("Inventories", null, values)
    }

    fun addNewInventoryPart(part: InventoryPart) {
        supplyInventoryPartIds(part)
        val database = databaseManager.writableDatabase
        val values = ContentValues()
        values.put("InventoryID", part.inventoryId)
        values.put("TypeID", part.typeId)
        values.put("ItemID", part.itemId)
        values.put("QuantityInSet", part.quantityInSet)
        values.put("ColorID", part.colorId)
        database.insert("InventoriesParts", null, values)
    }

    fun getLastInventoryId(): Int {
        val query = "select max(id) from Inventories;"
        val database = databaseManager.readableDatabase
        val cursor = database.rawQuery(query, null)
        var result = -1
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0)
        }
        closeCursor(cursor)
        return result
    }

    private fun supplyInventoryPartIds(inventoryPart: InventoryPart) {
        val query =
            "SELECT i.id,p.id,c.id FROM ItemTypes i INNER JOIN Parts p INNER JOIN Colors c" +
                    " WHERE i.code LIKE \"${inventoryPart.typeCode}\" AND p.code LIKE \"${inventoryPart.itemCode}\" AND c.code LIKE \"${inventoryPart.colorCode}\""
        val database = databaseManager.readableDatabase
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            inventoryPart.typeId = cursor.getInt(0)
            inventoryPart.itemId = cursor.getInt(1)
            inventoryPart.colorId = cursor.getInt(2)
        }
        closeCursor(cursor)
    }

//    Main Activity -------------------------------------------------

    fun getInventories(showArchived: Boolean = false): List<Inventory> {
        val inventories = ArrayList<SQLParser>()
        var query = "SELECT * FROM Inventories"
        query += if (showArchived) ";" else " WHERE ACTIVE=1;"
        execReadableQuery(query, inventories, Inventory::class.java)
        return inventories as ArrayList<Inventory>
    }

//    Inventory Properties Activity -------------------------------------------------

    fun getInventoryParts(inventoryId: Int): List<InventoryPart> {
        val inventoryParts = ArrayList<SQLParser>()
        val query =
            "SELECT * from InventoriesParts where InventoryID = $inventoryId"
        return execReadableQuery(
            query,
            inventoryParts,
            InventoryPart::class.java
        ) as ArrayList<InventoryPart>
    }

    fun updateInventoryStatusAndAccessDate(inventory: Inventory) {
        val query =
            "update Inventories set Active=" + inventory.active + ",LastAccessed=" + getTodayDate() + " where id=" + inventory.id + ";"
        execWritableQuery(query)
    }

    fun supplyPartsNames(parts: List<InventoryPart>) {
        parts.map {
            if (it.name == null) {
                val database = databaseManager.readableDatabase
                val query = "select Name, Code from Parts where id=${it.itemId}"
                val cursor = database.rawQuery(query, null)
                if (cursor.moveToFirst()) {
                    it.name = cursor.getString(0)
                    it.itemCode = cursor.getString(1)
                }
                closeCursor(cursor)
            }
        }
    }

    fun supplyPartsColors(parts: List<InventoryPart>) {
        parts.map {
            if (it.color == null) {
                val database = databaseManager.readableDatabase
                val query = "select Name, Code from Colors where id=\"${it.colorId}\""
                val cursor = database.rawQuery(query, null)
                if (cursor.moveToFirst()) {
                    it.color = cursor.getString(0)
                    it.colorCode = cursor.getInt(1)
                }
                closeCursor(cursor)
            }
        }
    }

    fun supplyDesignCodesAndImages(parts: List<InventoryPart>) {
        parts.map {
            if (it.designCode == null) {
                val database = databaseManager.readableDatabase
                val query =
                    "select Code from Codes where ColorID=${it.colorId} and ItemID=${it.itemId}"
                val cursor = database.rawQuery(query, null)
                if (cursor.moveToFirst()) {
                    it.designCode = cursor.getInt(0)
                    closeCursor(cursor)
                    supplyImage(it)
                }
            }
        }
    }

    private fun supplyImage(part: InventoryPart) {
        val database = databaseManager.readableDatabase
        val query = "select Image from Codes where Code=" + part.designCode + ";"
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val blob = cursor.getBlob(0)
            if (blob != null) {
                part.image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
            }
        }
        closeCursor(cursor)
    }

    fun updateImageInBLOB(part: InventoryPart) {
        ByteArrayOutputStream().use {
            part.image!!.compress(Bitmap.CompressFormat.JPEG, 100, it)
            val database = databaseManager.writableDatabase
            val values = ContentValues()
            values.put("Image", it.toByteArray())
            database.update("Codes", values, "Code=${part.designCode}", null)
        }
    }

    fun updateInventoryPart(part: InventoryPart) {
        val query =
            "update InventoriesParts set QuantityInStore=" + part.quantityInStore + " WHERE InventoryID=" + part.inventoryId + " AND ItemID=" + part.itemId + " AND ColorID=" + part.colorId + ";"
        execWritableQuery(query)
    }

    fun getTypeCode(part: InventoryPart): String? {
        val query = "SELECT Code FROM ItemTypes WHERE id=" + part.typeId + ";"
        return getCodeFromQuery(query)
    }

    fun getItemCode(part: InventoryPart): String? {
        val query = "SELECT Code FROM Parts WHERE id=" + part.itemId + ";"
        return getCodeFromQuery(query)
    }

    fun getColorCode(part: InventoryPart): String? {
        val query = "SELECT Code FROM Colors WHERE id=" + part.colorId + ";"
        return getCodeFromQuery(query)
    }

    private fun getCodeFromQuery(query: String): String? {
        val database = databaseManager.readableDatabase
        val cursor = database.rawQuery(query, null)
        var result: String? = null
        if (cursor.moveToFirst()) {
            result = cursor.getString(0)
        }
        closeCursor(cursor)
        return result
    }

    //    ---------------------------------------------------
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
        closeCursor(cursor)
        return resultList
    }

    private fun execWritableQuery(query: String) {
        val database = databaseManager.writableDatabase
        database.beginTransaction()
        database.execSQL(query)
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    private fun closeCursor(cursor: Cursor) {
        if (!cursor.isClosed) {
            cursor.close()
        }
    }
}
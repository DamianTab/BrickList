package damian.tab.bricklist.task

import android.os.AsyncTask
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import damian.tab.bricklist.database.SQLExecutor
import damian.tab.bricklist.domain.Inventory
import damian.tab.bricklist.getTodayDate
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class DownloadXMLAsyncTask(private var context: AppCompatActivity) : AsyncTask<String, Void, Int>() {

    override fun onPreExecute() {}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doInBackground(vararg arguments: String?): Int {
        if (arguments.size == 1) {
            try {
                HttpURLConnection.setFollowRedirects(false)
                val con = URL(arguments[0]).openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                return if (con.responseCode == HttpURLConnection.HTTP_OK) 0 else -1
            } catch (e: Exception) {
                e.printStackTrace()
                return -1
            }
        } else {
            if (SQLExecutor.checkIfInventoryExists(arguments[1])) return -2
            try {
                val url = URL(arguments[0])
                val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
                val db: DocumentBuilder = dbf.newDocumentBuilder()
                val doc: Document = db.parse(InputSource(url.openStream()))
                doc.documentElement.normalize()
                val items = doc.getElementsByTagName("ITEM")

                val newInventoryId = SQLExecutor.getLastInventoryId() + 1
                val newInventory = Inventory(newInventoryId, arguments[1]!!, 1, getTodayDate())
                SQLExecutor.addNewInventory(newInventory)

                for (i in 0 until items.length) {
                    val node = items.item(i)
                    SQLExecutor.addNewInventoryPart(node.childNodes, newInventory)
                }
                return 1
            } catch (e: Exception) {
                e.printStackTrace()
                return -1
            }
        }
    }

    override fun onPostExecute(result: Int) {
        val message = when (result) {
            0 -> "Settings found. Given id is correct."
            1 -> "Project added."
            -2 -> "Project with this name already exists"
            else -> "Given Inventory id or URL prefix is not correct (possibly you have problem with Internet connection)"
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        if (result == 1) context.finish()
    }
}
package damian.tab.bricklist.task

import android.graphics.BitmapFactory
import android.os.AsyncTask
import damian.tab.bricklist.IMAGE_URL_1
import damian.tab.bricklist.IMAGE_URL_2
import damian.tab.bricklist.IMAGE_URL_3
import damian.tab.bricklist.domain.InventoryPart
import java.net.URL

class DownloadImageAsyncTask(
    private val part: InventoryPart
) : AsyncTask<String, Void, Void>() {

    override fun doInBackground(vararg params: String?): Void? {
        //todo usunac
        println("++++++++++++++++++++++++ Sciaganie dla: " + part.name)
        if (part.image == null) {
            var url = URL(IMAGE_URL_1 + part.designCode)
            try {
                url.openConnection().getInputStream().use {
                    part.image = BitmapFactory.decodeStream(it)
                }
                //todo zapisanie zdjecia do bazy danych
            } catch (e: Exception) {
                url =
                    if (part.colorCode == null || part.colorCode == 0) URL(IMAGE_URL_2 + part.partCode + ".jpg")
                    else URL(IMAGE_URL_3 + part.colorCode + "/" + part.partCode + ".jpg")
                try {
                    url.openConnection().getInputStream().use {
                        part.image = BitmapFactory.decodeStream(it)
                    }
                } catch (e: Exception) {
                    //todo usunac
                    println("++++++++++++++++++++++++ Nie dziala inventoryPart o tej nazwie: " + part.name)
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}
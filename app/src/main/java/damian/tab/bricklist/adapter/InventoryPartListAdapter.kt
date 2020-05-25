package damian.tab.bricklist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import damian.tab.bricklist.R
import damian.tab.bricklist.domain.InventoryPart

class InventoryPartListAdapter(
    private val context: Context,
    private val inventoryParts: List<InventoryPart>
) : BaseAdapter() {

    private var inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return inventoryParts.size
    }

    override fun getItem(position: Int): InventoryPart {
        return inventoryParts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.inventory_part, parent, false)
        val nameTextView = rowView.findViewById(R.id.part_name) as TextView
        val quantityTextView = rowView.findViewById(R.id.part_quantity) as TextView
        val imageView = rowView.findViewById(R.id.part_image) as ImageView
        val inventoryPart = getItem(position)

        nameTextView.text = inventoryPart.name + "\n\n" + inventoryPart.color
        quantityTextView.text = generateQuantityText(inventoryPart)
        imageView.setImageBitmap(inventoryPart.image)

//        if (inventoryPart.designId != null)
//            Picasso.get()
//                .load("https://www.lego.com/service/bricks/5/2/" + inventoryPart.designId.toString())
//                .into(imageView, object : com.squareup.picasso.Callback {
//                    override fun onSuccess() {
//
//                    }
//
//                    override fun onError(e: java.lang.Exception?) {
//                        Picasso.get()
//                            .load("http://img.bricklink.com/P/${inventoryPart.colorCode.toString()}/${inventoryPart.designId.toString()}.gif")
//                            .into(imageView)
//                    }
//                })


        rowView.findViewById<FloatingActionButton>(R.id.plus_button).setOnClickListener {
            changeQuantity(inventoryPart, quantityTextView, position, 1)
        }
        rowView.findViewById<FloatingActionButton>(R.id.minus_button).setOnClickListener {
            changeQuantity(inventoryPart, quantityTextView, position, -1)
        }
        changeRowColor(inventoryPart, quantityTextView)

        return rowView
    }

    private fun changeQuantity(
        inventoryPart: InventoryPart,
        quantityTextView: TextView,
        position: Int,
        valueToAdd: Int
    ) {
        if (inventoryPart.quantityInSet > inventoryPart.quantityInStore) {
            inventoryPart.quantityInStore += valueToAdd
        }
        inventoryParts[position].quantityInStore = inventoryPart.quantityInStore
        quantityTextView.text = generateQuantityText(inventoryPart)
        changeRowColor(inventoryPart, quantityTextView)
    }

    private fun changeRowColor(inventoryPart: InventoryPart, quantityTextView: TextView) {
        if (inventoryPart.quantityInSet > inventoryPart.quantityInStore) {
            quantityTextView.setTextColor(Color.RED)
        } else {
            quantityTextView.setTextColor(Color.GRAY)
        }
    }

    private fun generateQuantityText(inventoryPart: InventoryPart): String {
        return "\n      " + (inventoryPart.quantityInStore).toString() + " of " + inventoryPart.quantityInSet.toString()
    }
}
package pe.com.globaltics.delivery.Views

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import pe.com.globaltics.delivery.Activitys.CarritoActivity
import pe.com.globaltics.delivery.Clases.Productos
import pe.com.globaltics.delivery.Clases.SQLite
import pe.com.globaltics.delivery.Clases.doIncrease
import pe.com.globaltics.delivery.R
import java.util.*

class ProductosAdaptador (
    private val prodList: ArrayList<Productos>,
    val context: Context)
    : RecyclerView.Adapter<ProductosAdaptador.ViewHolder>() {
    private  var sqlite:SQLite? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosAdaptador.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_productos, parent, false)
        sqlite = SQLite(context, "CarritoDB.sqlite", null, 1)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ProductosAdaptador.ViewHolder, position: Int) {
        holder.bindItems(prodList[position])

        holder.comprarv.setOnClickListener {

            val d = Dialog(context)
            d.setContentView(R.layout.dialog_comprar)
            d.setCanceledOnTouchOutside(false)

            val np = d.findViewById(R.id.np) as NumberPicker
            val confirmar = d.findViewById(R.id.confirmar) as Button
            np.minValue = 1
            np.maxValue = 10
            np.wrapSelectorWheel = true

            np.setOnValueChangedListener { _, _, _ -> }
            confirmar.setOnClickListener {
                val value = np.value
                try {
                    sqlite!!.insertData(prodList[position].id,prodList[position].nombre,prodList[position].precio,
                        prodList[position].descripcion,value,prodList[position].imagen )
                    //sqlite!!.close()
                    IncVal(d)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val window = d.window!!
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.CENTER)
            d.show()
        }
        holder.item.setOnClickListener {
            //Toast.makeText(context,"Click item",Toast.LENGTH_SHORT).show()

            val intent = Intent(context, CarritoActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("key", "fr_prod_detall")
            intent.putExtra("id",prodList[position].id.toString())
            intent.putExtra("precio",holder.preciov.text.toString())
            intent.putExtra("nombre",holder.nombrev.text.toString())
            intent.putExtra("foto",prodList[position].imagen)

            context.startActivity(intent)
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return prodList.size
    }
    private fun IncVal(d:Dialog) {
        //var id_producto = 0
        d.dismiss()
        try {
            (context as doIncrease).doIncreaseComplete(1
                //,id_producto
            )
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView.findViewById(R.id.item) as ConstraintLayout
        val preciov = itemView.findViewById(R.id.precio) as TextView
        val nombrev  = itemView.findViewById(R.id.nombre) as TextView
        val fotov   = itemView.findViewById(R.id.foto) as ImageView
        val comprarv   = itemView.findViewById(R.id.comprar) as Button
        //val descripcionv = itemView.findViewById(R.id.descripcion) as TextView
        fun bindItems(prodList: Productos) {

            /*val imagen = prodList.imagen
            val byteImage = android.util.Base64.decode(imagen, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size)
            fotov.setImageBitmap(bitmap)*/

            Picasso.get().load("http://"+prodList.imagen).into(fotov)
            preciov.text = prodList.precio
            nombrev.text = prodList.nombre
            //descripcionv.text = prodList.descripcion
        }
    }
}
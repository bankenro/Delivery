package pe.com.globaltics.delivery.Views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import pe.com.globaltics.delivery.Clases.Carrito
import pe.com.globaltics.delivery.Clases.SQLite
import pe.com.globaltics.delivery.Fragments.CarritoFragment
import pe.com.globaltics.delivery.R


class CarritoAdaptador(
    var carritoList: ArrayList<Carrito>,
    val context: Context,
    val carritoFragment: CarritoFragment
) : RecyclerView.Adapter<CarritoAdaptador.ViewHolder>() {
    private var sqlite: SQLite? = null
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CarritoAdaptador.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito, parent, false)
        sqlite = SQLite(context, "CarritoDB.sqlite", null, 1)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CarritoAdaptador.ViewHolder, position: Int) {
        holder.bindItems(carritoList[position])
        holder.delete.setOnClickListener {
            val newPosition = holder.adapterPosition
            try {
                sqlite!!.deleteData(carritoList[position].id)
                sqlite!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            notifyDataSetChanged()
            carritoList.removeAt(newPosition)
            notifyItemRemoved(newPosition)
            notifyItemRangeChanged(newPosition, carritoList.size)
            carritoFragment.Actualizar()
        }
    }

    override fun getItemCount(): Int {
        return carritoList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val item = itemView.findViewById(R.id.item) as ConstraintLayout
        val preciov = itemView.findViewById(R.id.precio) as TextView
        val nombrev = itemView.findViewById(R.id.nombre) as TextView
        val cantidadv = itemView.findViewById(R.id.cantidad) as TextView
        val descripcionv = itemView.findViewById(R.id.descripcion) as TextView
        val fotov = itemView.findViewById(R.id.foto) as ImageView
        val delete = itemView.findViewById(R.id.delete) as ImageButton
        fun bindItems(carritoList: Carrito) {

            /*val imagen = carritoList.imagen
            val byteImage = android.util.Base64.decode(imagen, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size)
            fotov.setImageBitmap(bitmap)*/
            Picasso.get().load("http://"+carritoList.imagen).into(fotov)
            val multi: Double = carritoList.precio_prod.toDouble() * carritoList.cantidad

            preciov.text = multi.toString()
            nombrev.text = carritoList.nombre_prod
            descripcionv.text = carritoList.descrip_prod
            cantidadv.text = carritoList.cantidad.toString()
        }
    }
}
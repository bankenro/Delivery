package pe.com.globaltics.delivery.Views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import pe.com.globaltics.delivery.Clases.DeliveryDetalles
import pe.com.globaltics.delivery.R

class DeliveryDetallesAdaptador(
    val deliverydetallList: ArrayList<DeliveryDetalles>,
    val context: Context
) : RecyclerView.Adapter<DeliveryDetallesAdaptador.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foto = itemView.findViewById(R.id.foto) as ImageView
        val nombre = itemView.findViewById(R.id.nombre) as TextView
        val fecha = itemView.findViewById(R.id.fecha) as TextView
        val cantidad = itemView.findViewById(R.id.cantidad) as TextView
        val precio = itemView.findViewById(R.id.precio) as TextView
        val total = itemView.findViewById(R.id.total) as TextView

        fun bindItems(deliverydetallList: DeliveryDetalles) {
            Picasso.get().load("http://"+deliverydetallList.imagen).into(foto)

            nombre.text = deliverydetallList.nombre
            fecha.text = deliverydetallList.fecha
            cantidad.text = deliverydetallList.cantidad
            precio.text = deliverydetallList.precio
            total.text = deliverydetallList.total

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): DeliveryDetallesAdaptador.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_delivery_detall, parent, false)
        return DeliveryDetallesAdaptador.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return deliverydetallList.size
    }

    override fun onBindViewHolder(holder: DeliveryDetallesAdaptador.ViewHolder, position: Int) {
        holder.bindItems(deliverydetallList[position])
    }
}
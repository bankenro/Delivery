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
import java.util.ArrayList

class MisPedidosDetallesAdaptador(
    val mispedidosdetallList: ArrayList<DeliveryDetalles>,
    val context: Context
) : RecyclerView.Adapter<MisPedidosDetallesAdaptador.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MisPedidosDetallesAdaptador.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_delivery_detall, parent, false)
        return MisPedidosDetallesAdaptador.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mispedidosdetallList.size
    }

    override fun onBindViewHolder(holder: MisPedidosDetallesAdaptador.ViewHolder, position: Int) {
        holder.bindItems(mispedidosdetallList[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foto = itemView.findViewById(R.id.foto) as ImageView
        val nombre = itemView.findViewById(R.id.nombre) as TextView
        val fecha = itemView.findViewById(R.id.fecha) as TextView
        val cantidad = itemView.findViewById(R.id.cantidad) as TextView
        val precio = itemView.findViewById(R.id.precio) as TextView
        val total = itemView.findViewById(R.id.total) as TextView

        fun bindItems(mispedidosdetallList: DeliveryDetalles) {
            Picasso.get().load("http://"+mispedidosdetallList.imagen).into(foto)

            nombre.text = mispedidosdetallList.nombre
            fecha.text = mispedidosdetallList.fecha
            cantidad.text = mispedidosdetallList.cantidad
            precio.text = mispedidosdetallList.precio
            total.text = mispedidosdetallList.total

        }
    }
}
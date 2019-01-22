package pe.com.globaltics.delivery.Views

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import pe.com.globaltics.delivery.Clases.MisPedidos
import pe.com.globaltics.delivery.Fragments.DeliveryDetallesFragment
import pe.com.globaltics.delivery.Fragments.MisPedidosDetallesFragment
import pe.com.globaltics.delivery.R

class MisPedidosAdaptador(
    val mispedidosList: ArrayList<MisPedidos>,
    val context: Context
) : RecyclerView.Adapter<MisPedidosAdaptador.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MisPedidosAdaptador.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_mis_pedidos, parent, false)
        return MisPedidosAdaptador.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mispedidosList.size
    }

    override fun onBindViewHolder(holder: MisPedidosAdaptador.ViewHolder, position: Int) {
        holder.bindItems(mispedidosList[position])
        holder.item.setOnClickListener{
            val bundle = Bundle()
            val fragment = MisPedidosDetallesFragment()
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contenedor, fragment)
            bundle.putString("accion", "mis_pedidos_detall")
            bundle.putString("id", mispedidosList[position].id)
            bundle.putString("estado", mispedidosList[position].estado)
            bundle.putString("total", mispedidosList[position].total)
            bundle.putString("fecha", mispedidosList[position].fecha)

            fragment.arguments = bundle
            fragmentTransaction.commit()
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView.findViewById(R.id.item) as ConstraintLayout
        val imagen = itemView.findViewById(R.id.imagen) as ImageView
        val id = itemView.findViewById(R.id.id) as TextView
        val nombree = itemView.findViewById(R.id.nombree) as TextView
        val total = itemView.findViewById(R.id.total) as TextView
        val fecha = itemView.findViewById(R.id.fecha) as TextView

        fun bindItems(mispedidosList: MisPedidos) {
            id.text = mispedidosList.id
            nombree.text = mispedidosList.estado
            total.text = mispedidosList.total
            fecha.text = mispedidosList.fecha
        }
    }
}
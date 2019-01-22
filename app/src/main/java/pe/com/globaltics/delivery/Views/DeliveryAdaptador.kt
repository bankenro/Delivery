package pe.com.globaltics.delivery.Views

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import pe.com.globaltics.delivery.Clases.Delivery
import pe.com.globaltics.delivery.Fragments.DeliveryDetallesFragment
import pe.com.globaltics.delivery.Fragments.DeliveryFragment
import pe.com.globaltics.delivery.Fragments.DialogsFragments.DialogAgregProd
import pe.com.globaltics.delivery.Fragments.DialogsFragments.DialogEditDeliv
import pe.com.globaltics.delivery.Fragments.Verificar_PhoneFragment
import pe.com.globaltics.delivery.R
import java.util.*

class DeliveryAdaptador(
    val deliveryList: ArrayList<Delivery>,
    val context: Context,
    val deliveryFragment: DeliveryFragment
) :
    RecyclerView.Adapter<DeliveryAdaptador.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, po: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_delivery, parent, false)
        return DeliveryAdaptador.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return deliveryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(deliveryList[position])

        holder.edit.setOnClickListener{view ->
            val popupMenu = PopupMenu(context, view)
            val bundle = Bundle()
            popupMenu.menuInflater.inflate(R.menu.menu_delivery, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editar -> {
                        val dialog = DialogEditDeliv()
                        dialog.setTargetFragment(deliveryFragment,1)
                        val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        bundle.putString("accion","actu_estad_deliv")
                        bundle.putString("nombreu",deliveryList[position].nombreu)
                        bundle.putString("id", deliveryList[position].id)
                        dialog.arguments = bundle
                        dialog.show(ft, "Editar Delivery")
                    }
                    R.id.detalles -> {
                        val fragment = DeliveryDetallesFragment()
                        val fragmentManager = (context as FragmentActivity).supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.contenedor, fragment)
                        bundle.putString("accion", "delivery_detall")
                        bundle.putString("id", deliveryList[position].id)
                        bundle.putString("nombree", deliveryList[position].nombree)
                        bundle.putString("total", deliveryList[position].total)
                        bundle.putString("nombreu", deliveryList[position].nombreu)
                        bundle.putString("telefono", deliveryList[position].telefono)
                        bundle.putString("direccion", deliveryList[position].direccion)
                        bundle.putString("longitud", deliveryList[position].longitud)
                        bundle.putString("latitud", deliveryList[position].latitud)

                        fragment.arguments = bundle
                        fragmentTransaction.commit()
                    }
                }

                false
            }
            popupMenu.show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edit = itemView.findViewById(R.id.edit) as ImageButton
        val imagen = itemView.findViewById(R.id.imagen) as ImageView
        val id = itemView.findViewById(R.id.id) as TextView
        val nombree = itemView.findViewById(R.id.nombree) as TextView
        val total = itemView.findViewById(R.id.total) as TextView
        val nombre = itemView.findViewById(R.id.nombre) as TextView
        val telefono = itemView.findViewById(R.id.telefono) as TextView
        val direccion = itemView.findViewById(R.id.direccion) as TextView

        fun bindItems(deliveryList: Delivery) {
            id.text = deliveryList.id
            nombree.text = deliveryList.nombree
            total.text = deliveryList.total
            nombre.text = deliveryList.nombreu
            telefono.text = deliveryList.telefono
            direccion.text = deliveryList.direccion
        }
    }
}
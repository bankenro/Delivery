package pe.com.globaltics.delivery.Views

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.EndPoints
import pe.com.globaltics.delivery.Clases.ProductosIngredientes
import pe.com.globaltics.delivery.Clases.VolleySingleton
import pe.com.globaltics.delivery.Fragments.ControlProdIngreFragment
import pe.com.globaltics.delivery.Fragments.DialogsFragments.*
import pe.com.globaltics.delivery.R
import java.util.*

class ProductosIngredientesAdaptador(
    private val productosingredienteslist: ArrayList<ProductosIngredientes>,
    private val context: Context,
    private val tarea: String,
    private val controlProdIngreFragment: ControlProdIngreFragment
) : RecyclerView.Adapter<ProductosIngredientesAdaptador.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ProductosIngredientesAdaptador.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_prod_ingr, parent, false)
        return ProductosIngredientesAdaptador.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return productosingredienteslist.size
    }

    override fun onBindViewHolder(holder: ProductosIngredientesAdaptador.ViewHolder, position: Int) {
        holder.bindItems(productosingredienteslist[position])

        holder.edit.setOnClickListener { view ->
            val popupMenu = PopupMenu(context, view)
            val bundle = Bundle()
            popupMenu.menuInflater.inflate(R.menu.menu_opciones, popupMenu.menu)
            if (Objects.equals(tarea,"sel_prod_contr")){
                popupMenu.menu.findItem(R.id.addIngre).isVisible = true
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.actualizar -> {
                        if (Objects.equals(tarea, "sel_prod_contr")) {
                            val dialog = DialogAgregProd()
                            dialog.setTargetFragment(controlProdIngreFragment,1)
                            val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                            bundle.putString("accion","act_prod")
                            bundle.putInt("id", productosingredienteslist[position].id)
                            bundle.putString("nombre", productosingredienteslist[position].nombre)
                            bundle.putString("descripcion", productosingredienteslist[position].descripcion)
                            bundle.putString("imagen", productosingredienteslist[position].imagen)
                            bundle.putString("cambiar", productosingredienteslist[position].cambiar)
                            dialog.arguments = bundle
                            dialog.show(ft, "Actualizar Producto")
                        } else if (Objects.equals(tarea, "sel_ingre_contr")) {
                            val dialog = DialogAgregIngre()
                            dialog.setTargetFragment(controlProdIngreFragment,1)
                            val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                            bundle.putString("accion","act_ingr")
                            bundle.putInt("id", productosingredienteslist[position].id)
                            bundle.putString("nombre", productosingredienteslist[position].nombre)
                            bundle.putString("descripcion", productosingredienteslist[position].descripcion)
                            bundle.putString("imagen", productosingredienteslist[position].imagen)
                            bundle.putString("cambiar", productosingredienteslist[position].cambiar)
                            dialog.arguments = bundle
                            dialog.show(ft, "Actualizar Ingrediente")
                        }
                    }
                    R.id.eliminar -> {
                        val newPosition = holder.adapterPosition
                        if (Objects.equals(tarea, "sel_prod_contr")) {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Desea eliminar")
                                .setMessage(("Presione OK para eliminar"))
                                .setPositiveButton(
                                    "OK"
                                ) { _, _ ->
                                    Eliminar("elim_prod", productosingredienteslist[position].id, newPosition)
                                }
                                .setNegativeButton("CANCELAR"
                                ) { _, _ ->
                                    builder.setCancelable(true)
                                }
                            builder.show()
                        } else if (Objects.equals(tarea, "sel_ingre_contr")) {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Desea eliminar")
                                .setMessage(("Presione OK para eliminar"))
                                .setPositiveButton(
                                    "OK"
                                ) { _, _ ->
                                    Eliminar("elim_ingr",productosingredienteslist[position].id,newPosition)
                                }
                                .setNegativeButton("CANCELAR"
                                ) { _, _ ->
                                    builder.setCancelable(true)
                                }
                            builder.show()
                        }
                    }
                    R.id.addIngre->{
                        val dialog = DialogAgregIngreProd()
                        val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        bundle.putInt("id", productosingredienteslist[position].id)
                        bundle.putString("nombre", productosingredienteslist[position].nombre)
                        bundle.putString("imagen", productosingredienteslist[position].imagen)
                        dialog.arguments = bundle
                        dialog.show(ft, "Agregar Ingrediente a Productos")
                    }
                }

                false
            }
            popupMenu.show()
        }
    }

    private fun Eliminar(
        tarea: String,
        id: Int,
        newPosition: Int
    ) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(context, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        notifyDataSetChanged()
                        productosingredienteslist.removeAt(newPosition)
                        notifyItemRemoved(newPosition)
                        notifyItemRangeChanged(newPosition, productosingredienteslist.size)
                    } else {
                        Toast.makeText(context, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Toast.makeText(context, error?.message, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "eliminar"
                params["tipo"] = tarea
                params["id"] = id.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cambiav = itemView.findViewById(R.id.cambia) as TextView
        val nombrev = itemView.findViewById(R.id.nombre) as TextView
        val fotov = itemView.findViewById(R.id.foto) as ImageView
        val descripcionv = itemView.findViewById(R.id.descripcion) as TextView
        val edit = itemView.findViewById(R.id.edit) as ImageButton

        fun bindItems(productosingredienteslist: ProductosIngredientes) {

            /*val imagen = productosingredienteslist.imagen
            val byteImage = android.util.Base64.decode(imagen, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size)
            fotov.setImageBitmap(bitmap)*/
//"http://"192.168.1.10/delivery1/func/imag/prods/prueba.png
            val dire1 = productosingredienteslist.imagen
            Picasso.get().load("http://$dire1").into(fotov)

            cambiav.text = productosingredienteslist.cambiar
            nombrev.text = productosingredienteslist.nombre
            descripcionv.text = productosingredienteslist.descripcion
        }
    }
}
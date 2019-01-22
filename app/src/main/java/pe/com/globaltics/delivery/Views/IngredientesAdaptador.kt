package pe.com.globaltics.delivery.Views

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.EndPoints
import pe.com.globaltics.delivery.Clases.Productos
import pe.com.globaltics.delivery.Clases.VolleySingleton
import pe.com.globaltics.delivery.R
import java.util.*

class IngredientesAdaptador(
    private val ingredientesList: ArrayList<Productos>,
    val context: Context,
    val tipou: String,
    val idprod: String
) :
    RecyclerView.Adapter<IngredientesAdaptador.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView.findViewById(R.id.item) as ConstraintLayout
        val preciov = itemView.findViewById(R.id.precio) as TextView
        val nombrev = itemView.findViewById(R.id.nombre) as TextView
        val descripcionv = itemView.findViewById(R.id.descripcion) as TextView
        val fotov = itemView.findViewById(R.id.foto) as ImageView
        fun bindItems(ingredientesList: Productos) {

            /*val imagen = productoslist.imagen
            val byteImage = android.util.Base64.decode(imagen, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size)
            fotov.setImageBitmap(bitmap)*/
            Picasso.get().load("http://"+ingredientesList.imagen).into(fotov)
            descripcionv.text = ingredientesList.descripcion
            preciov.text = ingredientesList.precio
            nombrev.text = ingredientesList.nombre
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): IngredientesAdaptador.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredientes, parent, false)
        return IngredientesAdaptador.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ingredientesList.size
    }

    override fun onBindViewHolder(holder: IngredientesAdaptador.ViewHolder, position: Int) {
        holder.bindItems(ingredientesList[position])
        if (Objects.equals(tipou, "superuser")) {
            holder.item.setOnClickListener {
                val d = Dialog(context)
                d.setContentView(R.layout.dialog_agregar_ingr_a_prod)
                d.setCanceledOnTouchOutside(false)
                val imagen = d.findViewById(R.id.imagen) as ImageView
                val nombre = d.findViewById(R.id.nombre) as TextView
                val precio = d.findViewById(R.id.precio) as TextView
                val agregar = d.findViewById(R.id.agregar) as Button
                Picasso.get().load(ingredientesList[position].imagen) to imagen
                nombre.text = ingredientesList[position].nombre
                precio.text = ingredientesList[position].precio
                agregar.setOnClickListener {
                    Agregar(d, idprod, ingredientesList[position].id)
                }

                val window = d.window!!
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                window.setGravity(Gravity.CENTER)
                d.show()
            }
        }
    }

    private fun Agregar(d: Dialog, idprod: String, idingr: Int) {

        if (idprod.isNotEmpty() && idingr.toString().isNotEmpty()) {
            val stringRequest = object : StringRequest(
                Request.Method.POST, EndPoints.URL_ROOT,
                Response.Listener<String> { response ->
                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(context, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                            d.dismiss()
                        } else {
                            Toast.makeText(context, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, error?.message, Toast.LENGTH_LONG).show()
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["accion"] = "add_ingre_prod"
                    params["id_prod"] = idprod
                    params["id_ingre"] = idingr.toString()
                    return params
                }
            }
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        } else {
            Toast.makeText(context, "datos vacios", Toast.LENGTH_SHORT).show()
        }
    }
}
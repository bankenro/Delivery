package pe.com.globaltics.delivery.Fragments


import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PointerIconCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import pe.com.globaltics.delivery.Views.IngredientesAdaptador
import java.util.HashMap

class Productos_DetallesFragment : Fragment() {

    private var imagen1: ImageView? = null
    private var precio: TextView? = null
    private var nombre: TextView? = null
    private var comprar: Button? = null
    private var ingredientes: RecyclerView? = null
    private var id = ""
    private val accion = "sel_ingr"
    private var productoslist: MutableList<Productos>? = null
    private var sharedPreferences: SharedPreferences? = null
    private var tipou: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_productos__detalles, container, false)

        imagen1 = view.findViewById(R.id.foto)
        precio = view.findViewById(R.id.precio)
        nombre = view.findViewById(R.id.nombre)
        comprar = view.findViewById(R.id.comprar)
        ingredientes = view.findViewById(R.id.ingredientes)

        sharedPreferences = activity?.getSharedPreferences("deliveryGTs", Context.MODE_PRIVATE)
        tipou = sharedPreferences!!.getString("tipou", "")

        if (arguments != null) {

            /*val imagen = arguments?.get("foto").toString()
            val byteImage = android.util.Base64.decode(imagen, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size)
            imagen1!!.setImageBitmap(bitmap)*/

            Picasso.get().load("http://"+arguments?.get("foto")).into(imagen1)
            id = arguments?.get("id").toString()
            precio!!.text = arguments?.get("precio").toString()
            nombre!!.text = arguments?.get("nombre").toString()

            productoslist = ArrayList()
            LlenarIngre(accion,id)
        }
        return view

    }

    fun LlenarIngre(accion:String,id: String) {
        ingredientes!!.adapter = null
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("ingre")
                        productoslist!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val artist = Productos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre"),
                                objectArtist.getString("imagen"),
                                objectArtist.getString("descripcion"),
                                objectArtist.getString("precio")
                            )
                            productoslist!!.add(artist)
                            val adapter =
                                IngredientesAdaptador(
                                    (productoslist as ArrayList<Productos>?)!!,
                                    this.activity!!,
                                    tipou!!,
                                    id
                                )
                            ingredientes!!.layoutManager = LinearLayoutManager(activity)
                            ingredientes!!.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                            ingredientes!!.adapter = adapter
                        }

                    } else {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = accion
                params["prod"] = id
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}

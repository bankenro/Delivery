package pe.com.globaltics.delivery.Fragments


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Activitys.ProductosActivity
import pe.com.globaltics.delivery.Clases.*
import pe.com.globaltics.delivery.Fragments.DialogsFragments.DialogMap

import pe.com.globaltics.delivery.R
import pe.com.globaltics.delivery.Views.CarritoAdaptador
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 *
 */
class CarritoFragment : Fragment(), View.OnClickListener, LatitudLongitud, ActualizarPrecio {

    private var sharedPreferences: SharedPreferences? = null
    private var carritolist: MutableList<Carrito>? = null
    private var rv: RecyclerView? = null
    private var comprar: Button? = null
    private var total: TextView? = null
    var dialog: DialogMap? = null
    var sqLite: SQLite? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_carrito, container, false)
        sqLite = SQLite(this.activity!!, "CarritoDB.sqlite", null, 1)
        rv = view.findViewById(R.id.recyclerView)
        comprar = view.findViewById(R.id.comprar)
        total = view.findViewById(R.id.textView2)

        sharedPreferences = activity?.getSharedPreferences("deliveryGTs", Context.MODE_PRIVATE)
        val cursor = sqLite!!.getData("SELECT * FROM carrito")

        carritolist = ArrayList()
        carritolist!!.clear()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val id_prod = cursor.getInt(1)
            val nombre_prod = cursor.getString(2)
            val precio_prod = cursor.getString(3)
            val descrip_prod = cursor.getString(4)
            val cantidad = cursor.getInt(5)
            val imagen = cursor.getString(6)

            val carrito = Carrito(
                id, id_prod, nombre_prod, precio_prod, descrip_prod, cantidad, imagen
            )
            carritolist!!.add(carrito)
        }
        val adapter = CarritoAdaptador((carritolist as ArrayList<Carrito>), this.activity!!, this)
        rv!!.layoutManager = LinearLayoutManager(activity)
        rv!!.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        rv!!.adapter = adapter

        MandarContador()

        comprar!!.setOnClickListener(this)
        return view
    }

    override fun Actualizar() {
        MandarContador()
    }

    override fun ObtenerLatitudLongitud(latitud: String, longitud: String) {
        if (carritolist != null) {
            for (carrito in this.carritolist!!) {
                Comprar(
                    sharedPreferences!!.getInt("telefono", 0).toString(),
                    carrito.id_prod.toString(),
                    latitud, longitud,
                    carrito.cantidad.toString(),
                    carrito.precio_prod
                )
            }
            MandarNotificacion(sharedPreferences!!.getInt("telefono", 0).toString())
        }
    }

    private fun MandarNotificacion(id: String) {
        val request = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener { //response ->
                //Log.w("Error1", response)
            },
            Response.ErrorListener { error -> Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "notificacion"
                params["id"] = id
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }

    private fun MandarContador() {
        total!!.text = Sumar().toString()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.comprar ->
                ComoprobarUbicacion()
        }
    }

    private fun ComoprobarUbicacion() {
        dialog = DialogMap()
        val ft = activity!!.supportFragmentManager.beginTransaction()
        dialog!!.setTargetFragment(this, 1)
        dialog!!.show(ft, "MI UBICACION")
    }

    private fun Sumar(): Double {
        var totala = 0.00
        var multi: Double
        if (carritolist != null) {
            for (carrito in this.carritolist!!) {
                multi = carrito.precio_prod.toDouble() * carrito.cantidad
                totala += multi
            }
            //Toast.makeText(activity, "asdasdasdas " + totala.toString(), Toast.LENGTH_SHORT).show()
        }
        return totala
    }

    private fun Comprar(
        id_usu: String,
        id_prod: String,
        latitud: String,
        longitud: String,
        cantidad: String,
        precio: String
    ) {
        val request = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener { response ->
                //Log.w("Error1", response)
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        sqLite!!.deleteAllData()
                        dialog!!.dismiss()
                        val intent = Intent(activity, ProductosActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
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
                params["accion"] = "r_compra"
                params["id_usu"] = id_usu
                params["id_prod"] = id_prod
                params["latitud"] = latitud
                params["longitud"] = longitud
                params["cantidad"] = cantidad
                params["precio"] = precio
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}

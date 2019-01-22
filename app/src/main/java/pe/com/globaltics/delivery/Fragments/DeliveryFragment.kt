package pe.com.globaltics.delivery.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.*

import pe.com.globaltics.delivery.R
import pe.com.globaltics.delivery.Views.DeliveryAdaptador
import pe.com.globaltics.delivery.Views.Spinner.CategoriasAdaptador
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class DeliveryFragment : Fragment(), AdapterView.OnItemSelectedListener, InterfaceDelivery {

    private var estado: Spinner? = null
    private var pedidos: RecyclerView? = null
    private var estadosList: MutableList<Categorias>? = null
    private var deliveryList: MutableList<Delivery>? = null
    private var accion: String? = ""
    private var estadoid: Int? = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delivery, container, false)

        estado = view.findViewById(R.id.estado)
        pedidos = view.findViewById(R.id.pedidos)
        if (arguments != null) {
            accion = arguments?.get("accion").toString()
        }

        deliveryList = ArrayList()
        estadosList = ArrayList()
        LlenarEstados()

        estado!!.onItemSelectedListener = this
        return view
    }

    override fun LlenarDelivery() {
        LlenarPedidos(estadoid!!)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            R.id.estado -> {
                estadoid = estadosList!![position].id
                LlenarPedidos(estadosList!![position].id)
            }
        }
    }

    private fun LlenarPedidos(id: Int) {
        pedidos!!.adapter = null
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("delivery")
                        deliveryList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val delivery = Delivery(
                                objectArtist.getString("nombreu"),
                                objectArtist.getString("telefono"),
                                objectArtist.getString("direccion"),
                                objectArtist.getInt("ide"),
                                objectArtist.getString("nombree"),
                                objectArtist.getString("id"),
                                objectArtist.getString("longitud"),
                                objectArtist.getString("latitud"),
                                objectArtist.getString("total")
                            )
                            deliveryList!!.add(delivery)
                        }
                        val adapter =
                            DeliveryAdaptador((deliveryList as ArrayList<Delivery>?)!!, this.activity!!, this)
                        pedidos!!.layoutManager = LinearLayoutManager(activity)
                        pedidos!!.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                        pedidos!!.adapter = adapter
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
                params["accion"] = accion!!
                params["estado"] = id.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun LlenarEstados() {
        estado!!.adapter = null
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("estados")
                        estadosList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val estados = Categorias(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre")
                            )
                            estadosList!!.add(estados)
                        }
                        val adapter = CategoriasAdaptador(this.activity!!, R.layout.item_sp, this.estadosList!!)
                        estado!!.adapter = adapter
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
                params["accion"] = "selec_estad"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}

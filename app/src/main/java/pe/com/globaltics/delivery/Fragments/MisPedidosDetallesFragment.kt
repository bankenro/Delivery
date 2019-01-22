package pe.com.globaltics.delivery.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.DeliveryDetalles
import pe.com.globaltics.delivery.Clases.EndPoints
import pe.com.globaltics.delivery.Clases.VolleySingleton

import pe.com.globaltics.delivery.R
import pe.com.globaltics.delivery.Views.MisPedidosDetallesAdaptador
import java.util.ArrayList
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MisPedidosDetallesFragment : Fragment() {

    private var id: TextView? = null
    private var nombre: TextView? = null
    private var total: TextView? = null
    private var fecha:TextView?=null
    private var mispedidos_detalle: RecyclerView? = null
    private var misPedidosDetallesList: MutableList<DeliveryDetalles>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mis_pedidos_detalles, container, false)

        id = view.findViewById(R.id.id)
        nombre = view.findViewById(R.id.nombree)
        total = view.findViewById(R.id.total)
        fecha = view.findViewById(R.id.fecha)
        mispedidos_detalle = view.findViewById(R.id.mispedidos_detalle)

        val idt = arguments?.get("id").toString()
        id!!.text = idt
        nombre!!.text = arguments?.get("estado").toString()
        total!!.text = arguments?.get("total").toString()
        fecha!!.text = arguments?.get("fecha").toString()

        misPedidosDetallesList = ArrayList()
        LlenarMisPedidos(arguments?.get("accion").toString(), idt)

        return view
    }

    private fun LlenarMisPedidos(accion: String, id: String) {
        mispedidos_detalle!!.adapter = null
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("mispedidos_detall")
                        misPedidosDetallesList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val misPedidosDetalles = DeliveryDetalles(
                                objectArtist.getString("nombrep"),
                                objectArtist.getString("imagen"),
                                objectArtist.getString("fecha"),
                                objectArtist.getString("cantidad"),
                                objectArtist.getString("precio"),
                                objectArtist.getString("total")
                            )
                            misPedidosDetallesList!!.add(misPedidosDetalles)
                        }
                        val adapter =
                            MisPedidosDetallesAdaptador(
                                (misPedidosDetallesList as ArrayList<DeliveryDetalles>?)!!,
                                this.activity!!
                            )
                        mispedidos_detalle!!.layoutManager = LinearLayoutManager(activity)
                        mispedidos_detalle!!.addItemDecoration(
                            DividerItemDecoration(
                                activity,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        mispedidos_detalle!!.adapter = adapter
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
                params["id"] = id
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}

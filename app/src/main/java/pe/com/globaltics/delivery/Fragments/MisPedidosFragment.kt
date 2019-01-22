package pe.com.globaltics.delivery.Fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.EndPoints
import pe.com.globaltics.delivery.Clases.MisPedidos
import pe.com.globaltics.delivery.Clases.VolleySingleton

import pe.com.globaltics.delivery.R
import pe.com.globaltics.delivery.Views.MisPedidosAdaptador
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MisPedidosFragment : Fragment() {
    private var mispedidos:RecyclerView?= null
    private var sharedPreferences: SharedPreferences? = null
    private var misPedidosList: MutableList<MisPedidos>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mis_pedidos, container, false)

        mispedidos = view.findViewById(R.id.mispedidos)
        misPedidosList = ArrayList()
        sharedPreferences = activity?.getSharedPreferences("deliveryGTs", Context.MODE_PRIVATE)

        LlenarPedidos(arguments?.get("accion").toString(),sharedPreferences!!.getInt("telefono", 0).toString())

        return view
    }

    private fun LlenarPedidos(accion:String,telefono:String) {
        mispedidos!!.adapter = null
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("mispedidos")
                        misPedidosList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val misPedidos = MisPedidos(
                                objectArtist.getString("id"),
                                objectArtist.getString("estado"),
                                objectArtist.getString("total"),
                                objectArtist.getString("fecha")
                            )
                            misPedidosList!!.add(misPedidos)
                        }
                        val adapter =
                            MisPedidosAdaptador((misPedidosList as ArrayList<MisPedidos>?)!!, this.activity!!)
                        mispedidos!!.layoutManager = LinearLayoutManager(activity)
                        mispedidos!!.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                        mispedidos!!.adapter = adapter
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
                params["id"] = telefono
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}

package pe.com.globaltics.delivery.Fragments


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.github.clans.fab.FloatingActionButton

import pe.com.globaltics.delivery.R
import com.github.clans.fab.FloatingActionMenu
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.DeliveryDetalles
import pe.com.globaltics.delivery.Clases.EndPoints
import pe.com.globaltics.delivery.Clases.VolleySingleton
import pe.com.globaltics.delivery.Views.DeliveryDetallesAdaptador
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class DeliveryDetallesFragment : Fragment(), View.OnClickListener {


    private var delivery_detall: RecyclerView? = null
    private var fab: FloatingActionMenu? = null
    private var ubicacion: FloatingActionButton? = null
    private var llamada: FloatingActionButton? = null
    private var deliverydetallList: MutableList<DeliveryDetalles>? = null
    private var accion: String = ""
    private var id: String = ""
    private var longitud: String = ""
    private var latitud: String = ""
    private var telefono: String = ""
    private var idt: TextView? = null
    private var nombree: TextView? = null
    private var total: TextView? = null
    private var nombre: TextView? = null
    private var telefonot: TextView? = null
    private var direccion: TextView? = null
    private var imagen: ImageView? = null
    private var CALL = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delivery_detalles, container, false)
        delivery_detall = view.findViewById(R.id.delivery_detall)

        ViewCompat.setNestedScrollingEnabled(delivery_detall!!, false)

        accion = arguments!!.getString("accion", "")
        id = arguments!!.getString("id", "")
        longitud = arguments!!.getString("longitud", "")
        latitud = arguments!!.getString("latitud", "")
        telefono = arguments!!.getString("telefono", "")

        fab = view.findViewById(R.id.fab)
        ubicacion = view.findViewById(R.id.ubicacion)
        llamada = view.findViewById(R.id.llamada)
        idt = view.findViewById(R.id.id)
        nombree = view.findViewById(R.id.nombree)
        total = view.findViewById(R.id.total)
        nombre = view.findViewById(R.id.nombre)
        telefonot = view.findViewById(R.id.telefono)
        direccion = view.findViewById(R.id.direccion)
        imagen = view.findViewById(R.id.imagen)


        idt!!.text = id
        nombree!!.text = arguments?.getString("nombree", "")
        total!!.text = arguments?.getString("total", "")
        nombre!!.text = arguments?.getString("nombreu", "")
        telefonot!!.text = arguments?.getString("telefono", "")
        direccion!!.text = arguments?.getString("direccion", "")

        deliverydetallList = ArrayList()
        LlenarDelDetall()

        fab!!.setOnClickListener(this)
        ubicacion!!.setOnClickListener(this)
        llamada!!.setOnClickListener(this)

        return view
    }

    private fun LlenarDelDetall() {
        delivery_detall!!.adapter = null
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("delivery_detall")
                        deliverydetallList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val deliveryDetalles = DeliveryDetalles(
                                objectArtist.getString("nombrep"),
                                objectArtist.getString("imagen"),
                                objectArtist.getString("fecha"),
                                objectArtist.getString("cantidad"),
                                objectArtist.getString("precio"),
                                objectArtist.getString("total")
                            )
                            deliverydetallList!!.add(deliveryDetalles)
                        }
                        val adapter =
                            DeliveryDetallesAdaptador(
                                (deliverydetallList as ArrayList<DeliveryDetalles>?)!!,
                                this.activity!!
                            )
                        delivery_detall!!.layoutManager = LinearLayoutManager(activity)
                        delivery_detall!!.addItemDecoration(
                            DividerItemDecoration(
                                activity,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        delivery_detall!!.adapter = adapter
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ubicacion -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud(Ubicación)"))
                startActivity(intent)
            }
            R.id.llamada->{
                val i = Intent(Intent.ACTION_CALL)
                i.data = Uri.parse("tel:$telefono")
                val permisos = arrayOf(android.Manifest.permission.CALL_PHONE)
                ActivityCompat.requestPermissions(activity!!, permisos, CALL)
                if (ActivityCompat.checkSelfPermission(
                        this.activity!!,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                activity!!.startActivity(i)
            }
        }
    }
}

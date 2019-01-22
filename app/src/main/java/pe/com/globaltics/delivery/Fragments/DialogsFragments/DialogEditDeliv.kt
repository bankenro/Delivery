package pe.com.globaltics.delivery.Fragments.DialogsFragments

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.firebase.iid.Registrar
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.*
import pe.com.globaltics.delivery.R
import pe.com.globaltics.delivery.Views.Spinner.CategoriasAdaptador
import java.util.HashMap

class DialogEditDeliv : DialogFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var estados: Spinner? = null
    private var actualizar: Button? = null
    private var idpedido: TextView? = null
    private var nombre: TextView? = null
    private var estadosList: MutableList<Categorias>? = null
    private var estadoid: Int? = 0
    private var accion: String? = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_fragment_edit_deliv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        estados = view.findViewById(R.id.estados)
        actualizar = view.findViewById(R.id.actualizar)
        idpedido = view.findViewById(R.id.idpedido)
        nombre = view.findViewById(R.id.nombre)

        estadosList = ArrayList()
        LlenarEstados()

        accion = arguments?.getString("accion", "")
        nombre!!.text = arguments?.getString("nombreu", "")
        idpedido!!.text = arguments?.getString("id", "")

        estados!!.onItemSelectedListener = this
        actualizar!!.setOnClickListener(this)
    }

    private fun LlenarEstados() {
        estados!!.adapter = null
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
                        estados!!.adapter = adapter
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

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        estadoid = estadosList!![position].id
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.actualizar ->
                Actualizar()
        }
    }

    private fun Actualizar() {
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        MandarNotificacion(idpedido!!.text.toString())
                    } else {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = accion!!
                params["id"] = idpedido!!.text.toString()
                params["estadoid"] = estadoid.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun MandarNotificacion(id: String) {
        val request = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener { response ->
                // Toast.makeText(activity!!,response,Toast.LENGTH_LONG).show()
                // Log.w("Error1",response)
                try {
                    (targetFragment as InterfaceDelivery).LlenarDelivery()
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                }
                dialog.dismiss()
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

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
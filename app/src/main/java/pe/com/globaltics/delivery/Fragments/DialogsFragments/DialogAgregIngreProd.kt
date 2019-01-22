package pe.com.globaltics.delivery.Fragments.DialogsFragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
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

class DialogAgregIngreProd : DialogFragment(), SearchView.OnQueryTextListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        return inflater.inflate(R.layout.dialog_fragment_agreg_ingr_prod, container, false)
    }

    private var imagen: ImageView? = null
    private var nombre: TextView? = null
    private var ingredientes: RecyclerView? = null
    private var buscarSV: SearchView? = null
    private var sharedPreferences: SharedPreferences? = null
    private var ingredientesList: MutableList<Productos>? = null
    private var id = ""
    private val accion = "sel_ingre_contr"
    private var tipou: String? = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagen = view.findViewById(R.id.imagen)
        nombre = view.findViewById(R.id.nombre)
        ingredientes = view.findViewById(R.id.ingredientes)
        buscarSV = view.findViewById(R.id.buscarSV)
        sharedPreferences = activity?.getSharedPreferences("deliveryGTs", Context.MODE_PRIVATE)
        tipou = sharedPreferences!!.getString("tipou", "")

        id = arguments?.get("id").toString()
        nombre!!.text = arguments?.get("nombre").toString()
        Picasso.get().load(arguments?.get("imagen").toString()) to imagen
        LlenarIngre(accion, id,"")

        ingredientes!!.layoutManager = LinearLayoutManager(activity)
        ingredientes!!.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        ingredientesList = ArrayList()
        buscarSV!!.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            LlenarIngre(accion, id,newText)
        } else {
            LlenarIngre(accion, id,"")
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun LlenarIngre(accion: String, id: String,newText:String ) {
        ingredientes!!.adapter = null
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("prodingr")
                        ingredientesList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val artist = Productos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre"),
                                objectArtist.getString("imagen"),
                                objectArtist.getString("descripcion"),
                                objectArtist.getString("cambiar")
                            )
                            ingredientesList!!.add(artist)
                        }
                        val adapter =
                            IngredientesAdaptador(
                                (ingredientesList as ArrayList<Productos>?)!!,
                                this.activity!!,
                                tipou!!,
                                id
                            )
                        ingredientes!!.layoutManager = LinearLayoutManager(activity)
                        ingredientes!!.addItemDecoration(
                            DividerItemDecoration(
                                activity,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        ingredientes!!.adapter = adapter
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
                params["buscar"] = newText
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
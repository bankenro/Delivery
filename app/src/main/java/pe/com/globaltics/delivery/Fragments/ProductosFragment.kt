package pe.com.globaltics.delivery.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.EndPoints
import pe.com.globaltics.delivery.Clases.Productos
import pe.com.globaltics.delivery.Clases.VolleySingleton

import pe.com.globaltics.delivery.R
import pe.com.globaltics.delivery.Views.ProductosAdaptador
import java.util.HashMap


/**
 * A simple [Fragment] subclass.
 *
 */
class ProductosFragment : Fragment(), SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
           LlenarRv(newText)
        } else {
            LlenarRv("")
        }
        return false
    }

    private var accion = "sel_pro"
    private var categoria = ""
    private var rv: RecyclerView? = null
    private var layoutManager: GridLayoutManager? = null
    private var productoslist: MutableList<Productos>? = null

    companion object {
        fun newInstance(categoria: String): ProductosFragment {
            val fragment = ProductosFragment()
            val bundle = Bundle()
            bundle.putString("accion", categoria)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_buscar, menu)
        val searchItem = menu!!.findItem(R.id.buscar)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = "Buscar"

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_productos, container, false)

        rv = view.findViewById(R.id.rv_productos)



        categoria = arguments?.get("accion").toString()

        /*when (accion.toInt()) {
            1 -> activity!!.title = "Entradas"
            2 -> activity!!.title = "Sopas"
            3 -> activity!!.title = "Platos Criollos"
            4 -> activity!!.title = "Pollos a la Brasa"
            5 -> activity!!.title = "Combos Pollo"
            6 -> activity!!.title = "Pesacados y Mariscos"
            7 -> activity!!.title = "Parrillas"
            8 -> activity!!.title = "Recomendaciones"
            9 -> activity!!.title = "Combos Parrilas"
            10 -> activity!!.title = "Postres"
            11 -> activity!!.title = "Gaseosas Cervezas"
            12 -> activity!!.title = "Jugos - Refrescos"
            13 -> activity!!.title = "Vinos"
            else -> activity!!.title = "Entradas"
        }*/

        productoslist = ArrayList()
        layoutManager = GridLayoutManager(activity, 2)
        rv!!.layoutManager = layoutManager

        //IncVal()
        LlenarRv("")
        return view
    }

    private fun LlenarRv(newText: String) {
        rv!!.adapter = null
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("productos")
                        productoslist!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val productos = Productos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre"),
                                objectArtist.getString("imagen"),
                                objectArtist.getString("descripcion"),
                                objectArtist.getString("precio")
                            )
                            productoslist!!.add(productos)
                        }
                        val adapter =
                            ProductosAdaptador((productoslist as ArrayList<Productos>?)!!, this.activity!!)
                        rv!!.adapter = adapter
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
                params["categoria"] = categoria
                params["buscar"] = newText
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}

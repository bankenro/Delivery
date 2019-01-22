package pe.com.globaltics.delivery.Fragments


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
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
import pe.com.globaltics.delivery.Clases.EndPoints.NOTIFICACION
import pe.com.globaltics.delivery.Clases.InterfaceControlProdIngre
import pe.com.globaltics.delivery.Clases.ProductosIngredientes
import pe.com.globaltics.delivery.Clases.VolleySingleton
import pe.com.globaltics.delivery.Fragments.DialogsFragments.DialogAgregIngre
import pe.com.globaltics.delivery.Fragments.DialogsFragments.DialogAgregProd

import pe.com.globaltics.delivery.R
import pe.com.globaltics.delivery.Views.ProductosIngredientesAdaptador
import java.util.*
import kotlin.collections.ArrayList


class ControlProdIngreFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener,InterfaceControlProdIngre {
    override fun LlenarProdIngr() {
        Descargar("")
    }
    private var prodingr: RecyclerView? = null
    private var add: FloatingActionButton? = null
    private var tarea: String = ""
    private var productosIngredienteslist: MutableList<ProductosIngredientes>? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_control_prod_ingre, container, false)

        prodingr = view.findViewById(R.id.prod_ingr)
        add = view.findViewById(R.id.add)

        if (arguments != null) {
            tarea = arguments?.get("accion").toString()
        }

        prodingr!!.layoutManager = LinearLayoutManager(activity)
        prodingr!!.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        productosIngredienteslist = ArrayList()
        Descargar("")

        prodingr!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) {
                    add!!.show()
                } else if (dy > 0) {
                    add!!.hide()
                }
            }
        })

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val datos = intent.getStringExtra("mensaje")
                Toast.makeText(activity, datos, Toast.LENGTH_SHORT).show()
            }
        }

        add!!.setOnClickListener(this)
        return view
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(broadcastReceiver!!, IntentFilter(NOTIFICACION))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver!!)
    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_buscar, menu)
        val searchItem = menu!!.findItem(R.id.buscar)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = "Buscar"
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add ->
                if (Objects.equals(tarea, "sel_prod_contr")) {
                    val bundle = Bundle()
                    val dialog = DialogAgregProd()
                    dialog.setTargetFragment(this, 1)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    bundle.putString("accion","agre_prod")
                    dialog.arguments = bundle
                    dialog.show(ft, "Agregar Producto")
                } else if (Objects.equals(tarea, "sel_ingre_contr")) {
                    val bundle = Bundle()
                    val dialog = DialogAgregIngre()
                    dialog.setTargetFragment(this, 1)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    bundle.putString("accion","agre_ingre")
                    dialog.arguments = bundle
                    dialog.show(ft, "Agregar Ingredientes")
                }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            Descargar(newText)
        }else{
            Descargar("")
        }
        return false
    }

    private fun Descargar(newText: String) {
        prodingr!!.adapter = null
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("prodingr")
                        productosIngredienteslist!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val productosingredientes = ProductosIngredientes(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre"),
                                objectArtist.getString("imagen"),
                                objectArtist.getString("descripcion"),
                                objectArtist.getString("cambiar")
                            )
                            productosIngredienteslist!!.add(productosingredientes)
                        }
                        val adapter =
                            ProductosIngredientesAdaptador((productosIngredienteslist as ArrayList<ProductosIngredientes>?)!!, this.activity!!,tarea,this)
                        prodingr!!.adapter = adapter
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
                params["accion"] = tarea
                params["buscar"] = newText
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}

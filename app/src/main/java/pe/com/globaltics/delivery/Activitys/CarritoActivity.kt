package pe.com.globaltics.delivery.Activitys

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import pe.com.globaltics.delivery.Clases.LocationPermisos
import pe.com.globaltics.delivery.Fragments.CarritoFragment
import pe.com.globaltics.delivery.Fragments.MisPedidosFragment
import pe.com.globaltics.delivery.Fragments.Productos_DetallesFragment
import pe.com.globaltics.delivery.R

class CarritoActivity : AppCompatActivity(),LocationPermisos {
    override fun permisos() {
        SolicitarPermisos()
    }

    var accion: String? = null
    var nombre: String? = null
    var precio: String? = null
    var foto: String? = null
    var id: String? = null
    internal val permsRequestCode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thirteen)

        //if (savedInstanceState == null) {
        val extras = intent.extras
        val bundle = Bundle()
        accion = extras!!.getString("key")
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        when (accion) {
            "fr_carrito" -> {
                val fragment = CarritoFragment()
                fragmentTransaction.replace(R.id.contenedor, fragment)
                fragmentTransaction.commit()
            }
            "fr_prod_detall" -> {
                id = extras.getString("id")
                nombre = extras.getString("nombre")
                precio = extras.getString("precio")
                foto = extras.getString("foto")

                val fragment = Productos_DetallesFragment()
                fragmentTransaction.replace(R.id.contenedor, fragment)
                bundle.putString("id",id)
                bundle.putString("nombre",nombre)
                bundle.putString("precio",precio)
                bundle.putString("foto",foto)
                fragment.arguments = bundle
                fragmentTransaction.commit()
            }
            "fr_mis_pedidos"->{
                val fragment = MisPedidosFragment()
                fragmentTransaction.replace(R.id.contenedor, fragment)
                bundle.putString("accion","mis_pedidos")
                fragment.arguments = bundle
                fragmentTransaction.commit()
            }
        }
        //}
    }
    private fun SolicitarPermisos() {
        val perms = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= 23) {
            val coarse = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            val fine = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            if (coarse != PackageManager.PERMISSION_GRANTED &&
                fine != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(perms, permsRequestCode)
            }
        }
    }
}

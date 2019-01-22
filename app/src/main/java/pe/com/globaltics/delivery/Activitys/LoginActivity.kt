package pe.com.globaltics.delivery.Activitys

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import pe.com.globaltics.delivery.R
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.EndPoints
import pe.com.globaltics.delivery.Clases.LocationPermisos
import pe.com.globaltics.delivery.Clases.SQLite
import pe.com.globaltics.delivery.Clases.VolleySingleton
import pe.com.globaltics.delivery.Fragments.Actualizar_PerfilFragment
import pe.com.globaltics.delivery.Fragments.LoginFragment
import pe.com.globaltics.delivery.Fragments.Verificar_PhoneFragment
import java.util.*


class LoginActivity : AppCompatActivity()
//, signInWithPhoneAuth
{
    private var mAuth: FirebaseAuth? = null
    var preferences: SharedPreferences? = null
    internal val permsRequestCode = 0
    private var tipou: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        SolicitarPermisos()

        mAuth = FirebaseAuth.getInstance()
        preferences = getSharedPreferences("deliveryGTs", Context.MODE_PRIVATE)

        val apellp = preferences!!.getString("apellp", "")
        val apellm = preferences!!.getString("apellm", "")
        val nomb = preferences!!.getString("nomb", "")
        val direcc = preferences!!.getString("direcc", "")
        val numb = preferences!!.getInt("numb", 0)
        val telefono = preferences!!.getInt("telefono", 0)
        val foto = preferences!!.getString("foto", "")

        /*val byteImage = Base64.decode(foto, Base64.DEFAULT)
        val foto1 = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size)
        fotot.setImageBitmap(foto1)*/
        val sqlite = SQLite(this@LoginActivity, "CarritoDB.sqlite", null, 1)
        //sqlite.queryData("drop table carrito")
        sqlite.queryData(
            "create table if not exists carrito " +
                    "(id integer primary key autoincrement, " +
                    "id_prod integer, " +
                    "nombre_prod varchar," +
                    "precio_prod varchar, " +
                    "descrip_prod varchar, " +
                    "cantidad integer, " +
                    "imagen varchar )"
        )
        sqlite.close()


        if (savedInstanceState == null) {
            if (apellp!!.isNotEmpty() && apellm!!.isNotEmpty() && nomb!!.isNotEmpty() && direcc!!.isNotEmpty()
                && numb != 0 && telefono != 0 && foto!!.isNotEmpty()
            ) {
                TraerTipoUsu(telefono)
            } else {
                val fragment = LoginFragment()
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.contenedor, fragment)
                fragmentTransaction.commit()
            }
        }
    }

    private fun SolicitarPermisos() {
        val perms = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CALL_PHONE,android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= 23) {
            val coarse = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            val fine = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            val call = checkSelfPermission(Manifest.permission.CALL_PHONE)
            val exter = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (coarse != PackageManager.PERMISSION_GRANTED &&
                fine != PackageManager.PERMISSION_GRANTED &&
                        call != PackageManager.PERMISSION_GRANTED &&
                exter != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(perms, permsRequestCode)
            }
        }
    }

    private fun TraerTipoUsu(telefono: Int) {
        val request = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {

                        Toast.makeText(this@LoginActivity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()

                        val array = obj.getJSONArray("usu")
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)

                            tipou = objectArtist.getString("tipou")
                            val apellp = objectArtist.getString("apellidop")
                            val apellm = objectArtist.getString("apellidom")
                            val nomb = objectArtist.getString("nomb")
                            val direcc = objectArtist.getString("direcc")
                            val numb = objectArtist.getString("numb")
                            val telefono0 = objectArtist.getString("telefono")
                            val foto0 = objectArtist.getString("foto")

                            val editor = preferences?.edit()

                            editor?.clear()?.apply()

                            editor?.putString("tipou", tipou)
                            editor?.putString("apellp", apellp)
                            editor?.putString("apellm", apellm)
                            editor?.putString("nomb", nomb)
                            editor?.putString("direcc", direcc)
                            editor?.putInt("numb", numb.toInt())
                            editor?.putInt("telefono", telefono0.toInt())
                            editor?.putString("foto", foto0)
                            editor?.apply()
                            editor?.commit()
                        }
                        if (Objects.equals(tipou, "cliente")) {
                            val intent = Intent(this@LoginActivity, ProductosActivity::class.java)
                            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        } else if (Objects.equals(tipou, "trabajador")) {
                            val intent = Intent(this@LoginActivity, ControlActivity::class.java)
                            intent.putExtra("key_control", "trab")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else if (Objects.equals(tipou, "superuser")) {
                            val intent = Intent(this@LoginActivity, ControlActivity::class.java)
                            intent.putExtra("key_control", "admi")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val fragment = LoginFragment()
                        val fragmentManager = supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.add(R.id.contenedor, fragment)
                        fragmentTransaction.commit()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@LoginActivity, error?.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "tipo_u"
                params["telefono"] = telefono.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }

    /*override fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    //verification successful we will start the profile activity
                    val intent = Intent(this@LoginActivity, ProductosActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                } else {

                    //verification unsuccessful.. display an error message

                    var message = "Somthing is wrong, we will fix it soon..."

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered..."
                    }

                    val snackbar = Snackbar.make(findViewById<View>(R.id.parent), message, Snackbar.LENGTH_LONG)
                    snackbar.setAction("Dismiss") { }
                    snackbar.show()
                }
            }
    }*/
}

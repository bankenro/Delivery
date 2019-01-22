package pe.com.globaltics.delivery.Fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Activitys.ProductosActivity
import pe.com.globaltics.delivery.Clases.EndPoints
import pe.com.globaltics.delivery.Clases.VolleySingleton

import pe.com.globaltics.delivery.R
import java.io.ByteArrayOutputStream
import java.util.HashMap


/**
 * A simple [Fragment] subclass.
 *
 */
class Actualizar_PerfilFragment : Fragment(), View.OnClickListener {

    private val REQUEST_IMAGE_CAPTURE = 1
    private var sharedPreferences: SharedPreferences? = null
    var numero: EditText? = null
    var direccion: EditText? = null
    var nombres: EditText? = null
    var apellidom: EditText? = null
    var apellidop: EditText? = null
    var registrar: Button? = null
    var foto: ImageView? = null
    var telefono = ""
    var token = ""
    var foto1 = ""
    private var bitmap: Bitmap? = null
    var GALLERY_PICTURE = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_actualizar__perfil, container, false)

        numero = view.findViewById(R.id.numero)
        direccion = view.findViewById(R.id.direccion)
        nombres = view.findViewById(R.id.nombres)
        apellidom = view.findViewById(R.id.apell_mat)
        apellidop = view.findViewById(R.id.apell_pat)
        registrar = view.findViewById(R.id.registrar)
        foto = view.findViewById(R.id.foto)

        if (arguments != null) {
            telefono = arguments?.get("telefono").toString()
            token = arguments?.get("token").toString()
        }
        sharedPreferences = activity?.getSharedPreferences("deliveryGTs", Context.MODE_PRIVATE)


        foto!!.setOnClickListener(this)
        registrar!!.setOnClickListener(this)
        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == GALLERY_PICTURE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "seleccionar Imagen"), GALLERY_PICTURE)
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bundle = data!!.extras!!
            bitmap = bundle.get("data") as Bitmap
            foto!!.setImageBitmap(bitmap)
        }
        if (requestCode == GALLERY_PICTURE && resultCode == Activity.RESULT_OK && data != null) {
            val filepath = data.data
            val inpuStream = activity!!.contentResolver.openInputStream(filepath!!)
            bitmap = BitmapFactory.decodeStream(inpuStream)
            foto!!.setImageBitmap(bitmap)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.registrar -> {
                ComprobarDatos()
            }
            R.id.foto -> {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("ELEGIR FOTO O TOMAR FOTO")
                    .setMessage(("ELIJA UNA OPCIÓN"))
                    .setPositiveButton(
                        "ELEGIR FOTO"
                    ) { _, _ ->
                        val permisos = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        ActivityCompat.requestPermissions(activity!!, permisos, GALLERY_PICTURE)
                        if (ActivityCompat.checkSelfPermission(
                                activity!!,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(Intent.createChooser(intent, "seleccionar Imagen"), GALLERY_PICTURE)
                        } else {
                            Toast.makeText(activity!!, "Active los permisos de almacenamiento", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    .setNegativeButton(
                        "TOMAR FOTO"
                    ) { _, _ ->
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        if (intent.resolveActivity(activity!!.packageManager) != null) {
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                builder.show()

            }
        }
    }

    private fun ComprobarDatos() {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(foto!!.width, foto!!.height, Bitmap.Config.RGB_565)
            /*val canvas = Canvas(bitmap!!)
            foto!!.draw(canvas)*/
        }
        foto1 = convertirfoto(bitmap!!)

        if (apellidop?.text.toString().isNotEmpty() && apellidom?.text.toString().isNotEmpty() &&
            nombres?.text.toString().isNotEmpty() && direccion?.text.toString().isNotEmpty() &&
            numero?.text.toString().isNotEmpty() && telefono.isNotEmpty() && foto1.isNotEmpty() && token.isNotEmpty()
        ) {
            RegistrarPerfil(
                apellidop?.text.toString().trim(), apellidom?.text.toString().trim(),
                nombres?.text.toString().trim(), direccion?.text.toString().trim(),
                numero?.text.toString().trim(), telefono, foto1,token
            )

        } else {
            Toast.makeText(activity, "datos vacios", Toast.LENGTH_SHORT).show()
        }
    }


    private fun convertirfoto(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream)
        val byteFormat = stream.toByteArray()
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP)
    }

    private fun RegistrarPerfil(
        apellp: String,
        apellm: String,
        nomb: String,
        direcc: String,
        numb: String,
        telefono: String,
        foto: String,
        token: String
    ) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        TraerTipoUsu(telefono.toInt())
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
                params["accion"] = "regusu"
                params["apellp"] = apellp
                params["apellm"] = apellm
                params["nomb"] = nomb
                params["direcc"] = direcc
                params["numb"] = numb
                params["telefono"] = telefono
                params["foto"] = foto
                params["token"] = token
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun TraerTipoUsu(telefono: Int) {
        val request = object : StringRequest(
            Request.Method.POST, EndPoints.URL_ROOT,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {

                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()

                        val array = obj.getJSONArray("usu")
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)

                            val tipou = objectArtist.getString("tipou")
                            val apellp = objectArtist.getString("apellidop")
                            val apellm = objectArtist.getString("apellidom")
                            val nomb = objectArtist.getString("nomb")
                            val direcc = objectArtist.getString("direcc")
                            val numb = objectArtist.getString("numb")
                            val telefono0 = objectArtist.getString("telefono")
                            val foto0 = objectArtist.getString("foto")

                            val editor = sharedPreferences?.edit()

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
                        val intent = Intent(activity, ProductosActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
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
                params["accion"] = "tipo_u"
                params["telefono"] = telefono.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}

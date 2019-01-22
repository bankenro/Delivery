package pe.com.globaltics.delivery.Fragments.DialogsFragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import pe.com.globaltics.delivery.Clases.EndPoints
import pe.com.globaltics.delivery.Clases.InterfaceControlProdIngre
import pe.com.globaltics.delivery.Clases.VolleySingleton
import pe.com.globaltics.delivery.R
import java.io.ByteArrayOutputStream
import java.util.*

class DialogAgregIngre : DialogFragment(), View.OnClickListener {

    private var imagen: ImageView? = null
    private var nombre: EditText? = null
    private var descripcion: EditText? = null
    private var precio: EditText? = null
    private var agregar: Button? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private var GALLERY_PICTURE = 0
    private var accion: String? = ""
    private var bitmap: Bitmap? = null
    private var nombrestr: String? = ""
    private var descripcionstr: String? = ""
    private var imagenstr: String? = ""
    private var cambiarstr: String? = ""
    var foto1 = ""
    private var id: Int? = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        return inflater.inflate(R.layout.dialog_fragment_agregingr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        precio = view.findViewById(R.id.precio)
        imagen = view.findViewById(R.id.imagen)
        nombre = view.findViewById(R.id.nombre)
        descripcion = view.findViewById(R.id.descripcion)
        agregar = view.findViewById(R.id.agregar)

        val bundle = arguments
        accion = bundle!!.getString("accion", "")
        id = bundle.getInt("id", 0)
        nombrestr = bundle.getString("nombre", "")
        descripcionstr = bundle.getString("descripcion", "")
        imagenstr = bundle.getString("imagen", "")
        cambiarstr = bundle.getString("cambiar", "")

        if (Objects.equals(accion, "act_ingr")) {
            agregar!!.text = activity!!.resources.getText(R.string.actualizar)
            Picasso.get().load(imagenstr).into(imagen)
            nombre!!.setText(nombrestr)
            descripcion!!.setText(descripcionstr)
            precio!!.setText(cambiarstr)
        }

        imagen!!.setOnClickListener(this)
        agregar!!.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.agregar ->
                if (Objects.equals(accion, "agre_ingre")) {
                    Agregar()
                } else if (Objects.equals(accion, "act_ingr")) {
                    Actualizar()
                }
            R.id.imagen -> {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bundle = data!!.extras!!
            bitmap = bundle.get("data") as Bitmap
            imagen!!.setImageBitmap(bitmap)
        }
        if (requestCode == GALLERY_PICTURE && resultCode == Activity.RESULT_OK && data != null) {
            val filepath = data.data
            val inpuStream = activity!!.contentResolver.openInputStream(filepath!!)
            bitmap = BitmapFactory.decodeStream(inpuStream)
            imagen!!.setImageBitmap(bitmap)
        }
    }

    private fun convertirfoto(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream)
        val byteFormat = stream.toByteArray()
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP)
    }

    private fun Actualizar() {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(imagen!!.width, imagen!!.height, Bitmap.Config.RGB_565)
            /*val canvas = Canvas(bitmap!!)
            foto!!.draw(canvas)*/
        }
        foto1 = convertirfoto(bitmap!!)

        if (nombre?.text.toString().isNotEmpty() && descripcion?.text.toString().isNotEmpty()
            && foto1.isNotEmpty() && precio?.toString()!!.isNotEmpty()
        ) {
            val stringRequest = object : StringRequest(
                Request.Method.POST, EndPoints.URL_ROOT,
                Response.Listener<String> { response ->
                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                            try {
                                (targetFragment as InterfaceControlProdIngre).LlenarProdIngr()
                            } catch (e: ClassCastException) {
                                e.printStackTrace()
                            }
                            dialog.dismiss()
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
                    params["id"] = id!!.toString()
                    params["nombre"] = nombre!!.text.toString().trim()
                    params["descripcion"] = descripcion!!.text.toString().trim()
                    params["imagen"] = foto1
                    params["precio"] = precio!!.text.toString().trim()
                    return params
                }
            }
            VolleySingleton.instance?.addToRequestQueue(stringRequest)

        } else {
            Toast.makeText(activity, "datos vacios", Toast.LENGTH_SHORT).show()
        }

    }

    private fun Agregar() {

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(imagen!!.width, imagen!!.height, Bitmap.Config.RGB_565)
            /*val canvas = Canvas(bitmap!!)
            foto!!.draw(canvas)*/
        }
        foto1 = convertirfoto(bitmap!!)

        if (nombre?.text.toString().isNotEmpty() && descripcion?.text.toString().isNotEmpty()
            && foto1.isNotEmpty()
        ) {
            val stringRequest = object : StringRequest(
                Request.Method.POST, EndPoints.URL_ROOT,
                Response.Listener<String> { response ->
                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                            try {
                                (targetFragment as InterfaceControlProdIngre).LlenarProdIngr()
                            } catch (e: ClassCastException) {
                                e.printStackTrace()
                            }
                            dialog.dismiss()
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
                    params["nombre"] = nombre!!.text.toString().trim()
                    params["descripcion"] = descripcion!!.text.toString().trim()
                    params["imagen"] = foto1
                    params["precio"] = precio!!.text.toString().trim()
                    return params
                }
            }
            VolleySingleton.instance?.addToRequestQueue(stringRequest)

        } else {
            Toast.makeText(activity, "datos vacios", Toast.LENGTH_SHORT).show()
        }
    }

}
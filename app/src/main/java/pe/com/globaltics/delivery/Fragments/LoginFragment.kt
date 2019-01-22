package pe.com.globaltics.delivery.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONException
import org.json.JSONObject

import pe.com.globaltics.delivery.R
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class LoginFragment : Fragment(), View.OnClickListener {


    var telefono: EditText? = null
    var ingresar: Button? = null
    var TOKEN: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)

        telefono = view.findViewById(R.id.telefono)
        ingresar = view.findViewById(R.id.ingresar)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(
            (
                activity!!
            )
        ) { instanceIdResult ->
            TOKEN = instanceIdResult.token
            Log.e("Token", TOKEN)
        }

        ingresar!!.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ingresar -> {
                val mobile = telefono!!.text.toString().trim()
                if (mobile.isEmpty() || mobile.length < 9) {
                    telefono!!.error = "Ingresa un numero valido"
                    telefono!!.requestFocus()
                } else {
                    if (TOKEN != null) {
                        if (TOKEN!!.contains("{")) {
                            try {
                                val jo = JSONObject(TOKEN)
                                val nuevotoken = jo.getString("token")
                                val bundle = Bundle()
                                val fragment = Verificar_PhoneFragment()
                                val fragmentManager = activity!!.supportFragmentManager
                                val fragmentTransaction = fragmentManager.beginTransaction()
                                fragmentTransaction.replace(R.id.contenedor, fragment)
                                bundle.putString("telefono", mobile)
                                bundle.putString("token", nuevotoken)
                                fragment.arguments = bundle
                                fragmentTransaction.commit()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        } else {
                            val bundle = Bundle()
                            val fragment = Verificar_PhoneFragment()
                            val fragmentManager = activity!!.supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.contenedor, fragment)
                            bundle.putString("telefono", mobile)
                            bundle.putString("token", TOKEN)
                            fragment.arguments = bundle
                            fragmentTransaction.commit()
                        }
                    }
                }
            }
        }
    }
}

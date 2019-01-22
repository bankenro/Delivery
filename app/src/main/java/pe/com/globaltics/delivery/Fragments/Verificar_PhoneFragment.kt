package pe.com.globaltics.delivery.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import pe.com.globaltics.delivery.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.android.gms.tasks.TaskExecutors
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException


/**
 * A simple [Fragment] subclass.
 *
 */

class Verificar_PhoneFragment : Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.verificar -> {
                val codigo = cod_ver!!.text.toString().trim()
                if (codigo.isEmpty() || codigo.length < 6) {
                    cod_ver!!.error = "Ingresa un codigo valido"
                    cod_ver!!.requestFocus()
                } else {
                    verifyVerificationCode(codigo)
                }
            }
        }
    }

    private var mVerificationId: String? = null
    private var mAuth: FirebaseAuth? = null
    private var cod_ver: EditText? = null
    private var verificar: Button? = null
    private var telefono = ""
    private var token = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_verificar__phone, container, false)

        mAuth = FirebaseAuth.getInstance()
        cod_ver = view.findViewById(R.id.cod_ver)
        verificar = view.findViewById(R.id.verificar)

        if (arguments != null) {
            telefono = arguments?.get("telefono").toString()
            token = arguments?.get("token").toString()
            sendVerificationCode(telefono)
        }

        verificar!!.setOnClickListener(this)

        return view
    }

    private fun sendVerificationCode(mobile: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+51$mobile",
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks
        )
    }

    //the callback to detect the verification status
    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            //Getting the code sent by SMS
            val code = phoneAuthCredential.smsCode

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                cod_ver!!.setText(code)
                //verifying the code
                verifyVerificationCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(s: String?, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(s, forceResendingToken)

            //storing the verification id that is sent to the user
            mVerificationId = s
        }
    }


    private fun verifyVerificationCode(code: String?) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(this.mVerificationId!!, code!!)
        signInWithPhoneAuthCredential(credential)
        //signing the user
        /*try {
            (activity as signInWithPhoneAuth).signInWithPhoneAuthCredential(credential)

        } catch (e: ClassCastException) {
            e.printStackTrace()
        }*/
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                activity!!
            ) { task ->
                if (task.isSuccessful) {
                    //val intent = Intent(activity, ProductosActivity::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    //startActivity(intent)
                    //(activity as Activity).finish()
                    //activity!!.finish()
                    val bundle = Bundle()
                    val fragment = Actualizar_PerfilFragment()
                    val fragmentManager = activity!!.supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.contenedor, fragment)
                    bundle.putString("telefono", telefono)
                    bundle.putString("token", token)
                    fragment.arguments = bundle
                    fragmentTransaction.commit()
                } else {
                    var message = "Algo salio mal"

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = "Codigo invalido."
                    }
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

                }
            }
    }

}

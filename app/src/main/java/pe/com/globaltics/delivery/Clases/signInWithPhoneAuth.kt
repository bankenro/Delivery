package pe.com.globaltics.delivery.Clases

import com.google.firebase.auth.PhoneAuthCredential

interface signInWithPhoneAuth {
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
    }
}
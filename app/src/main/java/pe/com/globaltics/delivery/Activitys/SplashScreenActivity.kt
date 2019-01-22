package pe.com.globaltics.delivery.Activitys

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import pe.com.globaltics.delivery.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_splash_screen)
        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = getSplashScreenDuration()
        Handler().postDelayed(
            {
                // After the splash screen duration, route to the right activities
                //val user = UserDb.getCurrentUser()
                //routeToAppropriatePage(user)
            },
            splashScreenDuration
        )
    }

    private fun getSplashScreenDuration() = 2000L

    /*private fun routeToAppropriatePage(user: User) {
        // Example routing
        when {
            user == null -> OnboardingActivity.start(this)
            user.hasPhoneNumber() -> EditProfileActivity.start(this)
            user.hasSubscriptionExpired() -> PaymentPlansActivity.start(this)
            else -> HomeActivity.start(this)
        }
    }*/
}

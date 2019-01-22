package pe.com.globaltics.delivery.Activitys

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.app_bar_control.*
import pe.com.globaltics.delivery.Fragments.ControlProdIngreFragment
import pe.com.globaltics.delivery.Fragments.DeliveryFragment
import pe.com.globaltics.delivery.R

class ControlActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var accion: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        accion = extras!!.getString("key_control")
        when (accion) {
            "trab" -> {
                nav_view.menu.setGroupVisible(R.id.pedidos, true)
            }
            "admi" -> {
                nav_view.menu.setGroupVisible(R.id.control, true)
            }
        }
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        } else {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
                supportFragmentManager.beginTransaction().commit()
            } else {
                super.onBackPressed()
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

   /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.control, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        var fragmentTransaction = false
        var fragment: Fragment? = null
        val bundle = Bundle()
        when (item.itemId) {
            R.id.agre_prod -> {
                fragment = ControlProdIngreFragment()
                fragmentTransaction = true
                bundle.putString("accion", "sel_prod_contr")
                fragment.arguments = bundle            }
            R.id.agre_ingre -> {
                fragment = ControlProdIngreFragment()
                fragmentTransaction = true
                bundle.putString("accion", "sel_ingre_contr")
                fragment.arguments = bundle
            }
            R.id.delivery -> {
                fragment = DeliveryFragment()
                fragmentTransaction = true
                bundle.putString("accion", "delivery")
                fragment.arguments = bundle
            }
        }
        if (fragmentTransaction) {
            supportFragmentManager.beginTransaction().replace(R.id.contenedor, fragment!!).commit()
            item.isChecked = true
            title = item.title
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

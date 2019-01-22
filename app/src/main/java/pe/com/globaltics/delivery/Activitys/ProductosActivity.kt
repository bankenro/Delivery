package pe.com.globaltics.delivery.Activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu

import pe.com.globaltics.delivery.Clases.AdaptadorFragment
import pe.com.globaltics.delivery.R
import android.view.MenuItem
import android.widget.TextView
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Toast
import pe.com.globaltics.delivery.Clases.SQLite
import pe.com.globaltics.delivery.Clases.doIncrease

class ProductosActivity : AppCompatActivity(), doIncrease {
    override fun doIncreaseComplete(position: Int) {
        count += position

        /*val carrito1 = Carrito()

        //setter
        //carrito1.id = 1
        carrito1.id = id_producto
        //geter
        //println(carrito1.id)

        //add
        this.carrito!!.add(carrito1)
        //clear
        //carrito!!.clear()*/
        invalidateOptionsMenu()
    }

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var adapter: AdaptadorFragment? = null
    var contador: TextView? = null
    //var carrito: ArrayList<Carrito>? = null
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //supportActionBar!!.title = "TabLayout Demo"

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        //carrito = ArrayList()

        InicAdap(viewPager)
        Contador()

    }

    override fun onRestart() {
        super.onRestart()
        finish()
        Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun Contador() {
        val sqLite = SQLite(this, "CarritoDB.sqlite", null, 1)
        val cursor = sqLite.getData("SELECT * FROM carrito")
        count = cursor.count
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_carrito, menu)

        val carrito = menu!!.findItem(R.id.carrito)
        val pedidos = menu.findItem(R.id.pedidos)
        carrito.icon = buildCounterDrawable(count, R.drawable.carrito)

        return true
    }

    @SuppressLint("InflateParams")
    private fun buildCounterDrawable(count: Int, backgroundImageId: Int): Drawable {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.action_bar_notifitcation_icon, null)
        view.setBackgroundResource(backgroundImageId)

        if (count == 0) {
            val counterTextPanel: View = view.findViewById(R.id.counterValuePanel)
            counterTextPanel.visibility = View.GONE
        } else {
            contador = view.findViewById(R.id.contador) as TextView
            contador!!.text = count.toString()
        }

        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        view.isDrawingCacheEnabled = true
        view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false

        return BitmapDrawable(resources, bitmap)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.carrito -> {
                if (count == 0){
                    Toast.makeText(this,"Realize una compra",Toast.LENGTH_SHORT).show()
                }else{
                    val intent = Intent(this@ProductosActivity, CarritoActivity::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("key", "fr_carrito")
                    startActivity(intent)
                    //finish()
                }
            }
            R.id.pedidos -> {
                val intent = Intent(this@ProductosActivity, CarritoActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("key", "fr_mis_pedidos")
                startActivity(intent)
                //finish()
            }
        }
        return true
    }

    private fun InicAdap(viewPager: ViewPager?) {
        adapter = AdaptadorFragment(supportFragmentManager)
        viewPager!!.adapter = adapter

        /*val f1 = ProductosFragment.newInstance("1")
        adapter!!.addFragment(f1, "TAB 1")

        val f2 = LoginFragment.newInstance("One")
        adapter!!.addFragment(f2, "TAB 1")*/

        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.getTabAt(0)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(1)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(2)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(3)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(4)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(5)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(6)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(7)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(8)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(9)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(10)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(11)!!.setIcon(android.R.drawable.ic_dialog_email)
        tabLayout!!.getTabAt(12)!!.setIcon(android.R.drawable.ic_dialog_email)
    }

}



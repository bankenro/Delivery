package pe.com.globaltics.delivery.Clases

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import pe.com.globaltics.delivery.Fragments.ProductosFragment

class AdaptadorFragment(
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment = when (p0){
        0 -> ProductosFragment.newInstance("1")
        1 -> ProductosFragment.newInstance("2")
        2 -> ProductosFragment.newInstance("3")
        3 -> ProductosFragment.newInstance("4")
        4 -> ProductosFragment.newInstance("5")
        5 -> ProductosFragment.newInstance("6")
        6 -> ProductosFragment.newInstance("7")
        7 -> ProductosFragment.newInstance("8")
        8 -> ProductosFragment.newInstance("9")
        9 -> ProductosFragment.newInstance("10")
        10 -> ProductosFragment.newInstance("11")
        11 -> ProductosFragment.newInstance("12")
        12 -> ProductosFragment.newInstance("13")

        else -> ProductosFragment.newInstance("1")
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Entradas"
        1 -> "Sopas"
        2 -> "Platos Criollos"
        3 -> "Pollos a la Brasa"
        4 -> "Combos Pollo"
        5 -> "Pesacados y Mariscos"
        6 -> "Parrillas"
        7 -> "Recomendaciones"
        8 -> "Combos Parrilas"
        9 -> "Postres"
        10 -> "Gaseosas Cervezas"
        11 -> "Jugos - Refrescos"
        12 -> "Vinos"

        else -> "Entradas"
    }

    override fun getCount(): Int = 13


    /*val fragments = ArrayList <Fragment>()
    val titles = ArrayList<String>()

    override fun getItem(position: Int): Fragment = fragments.get(position)

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = titles.get(position)

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)
    }*/
}

package pe.com.globaltics.delivery.Views.Spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import pe.com.globaltics.delivery.Clases.Categorias
import pe.com.globaltics.delivery.R

class CategoriasAdaptador(private val c: Context, private val mResource: Int, private val nombres: List<Categorias>) :
    ArrayAdapter<Categorias>(c, mResource, nombres) {
    private val mInflater: LayoutInflater = LayoutInflater.from(c)

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        return createItemView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = mInflater.inflate(mResource, parent, false)

        val nombrestxt = view.findViewById(R.id.nombre) as TextView

        val nombres1 = nombres[position]
        nombrestxt.text = nombres1.nombre

        return view
    }

}

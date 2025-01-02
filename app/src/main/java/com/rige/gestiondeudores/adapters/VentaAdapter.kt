package com.rige.gestiondeudores.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.models.Venta

class VentaAdapter(
    private val context: Context,
    private val ventas: List<Venta>
) : BaseAdapter() {

    override fun getCount(): Int = ventas.size

    override fun getItem(position: Int): Any = ventas[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_venta, parent, false)

        val venta = ventas[position]

        val tvMonto = view.findViewById<TextView>(R.id.tvMonto)
        val tvFecha = view.findViewById<TextView>(R.id.tvFecha)
        val ivEstado = view.findViewById<ImageView>(R.id.ivEstado)

        tvMonto.text = "Monto: ${venta.monto}"
        tvFecha.text = "Fecha: ${venta.fechaVenta}"

        if (venta.estado) {
            ivEstado.setImageResource(R.drawable.check_icon)
        } else {
            ivEstado.setImageResource(R.drawable.cross_icon)
        }

        return view
    }
}
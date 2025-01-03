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

class VentaClienteAdapter(
    private val context: Context,
    private val ventas: List<Venta>,
    private val onEditarClick: (id: Int) -> Unit,
    private val onVentaClick: (id: Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = ventas.size

    override fun getItem(position: Int): Any = ventas[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_venta_cliente, parent, false)

        val venta = ventas[position]

        val tvMonto = view.findViewById<TextView>(R.id.tvMonto)
        val tvFecha = view.findViewById<TextView>(R.id.tvFecha)
        val ivEstado = view.findViewById<ImageView>(R.id.ivEstado)
        val ivAlternar = view.findViewById<ImageView>(R.id.ivAlternar)

        tvMonto.text = "Monto: s/. ${venta.monto}"
        tvFecha.text = "Fecha: ${venta.fechaVenta}"

        if (venta.estado) {
            ivEstado.setImageResource(R.drawable.check_icon)
            ivAlternar.visibility = View.INVISIBLE
        } else {
            ivEstado.setImageResource(R.drawable.cross_icon)
            ivAlternar.visibility = View.VISIBLE
            ivAlternar.setOnClickListener { onEditarClick(venta.id ?: 0) }
        }

        view.setOnClickListener { onVentaClick(venta.id ?: 0) }

        return view
    }
}
package com.rige.gestiondeudores.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.models.custom.VentaCliente

class VentaAdapter(
    private val context: Context,
    private val ventas: List<VentaCliente>,
    private val onEditarClick: (VentaCliente) -> Unit,
    private val onVentaClick: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = ventas.size

    override fun getItem(position: Int): Any = ventas[position]

    override fun getItemId(position: Int): Long = ventas[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_venta, parent, false)

        val venta = ventas[position]

        val ivEstado = view.findViewById<ImageView>(R.id.ivEstado)
        val tvMonto = view.findViewById<TextView>(R.id.tvMonto)
        val tvFechaVenta = view.findViewById<TextView>(R.id.tvFechaVenta)
        val tvNombreCliente = view.findViewById<TextView>(R.id.tvNombreCliente)
        val ivAlternar = view.findViewById<ImageView>(R.id.ivAlternar)

        tvMonto.text = "Monto: s/. ${venta.monto}"
        tvFechaVenta.text = "Fecha: ${venta.fechaVenta}"
        tvNombreCliente.text = "Cliente: ${venta.nombreCliente}"

        ivEstado.setImageResource(
            if (venta.estado) R.drawable.check_icon else R.drawable.cross_icon
        )

        if (venta.estado) {
            ivAlternar.visibility = View.GONE
        } else {
            ivAlternar.visibility = View.VISIBLE
            ivAlternar.setOnClickListener { onEditarClick(venta) }
        }

        view.setOnClickListener { onVentaClick(venta.id) }

        return view
    }
}

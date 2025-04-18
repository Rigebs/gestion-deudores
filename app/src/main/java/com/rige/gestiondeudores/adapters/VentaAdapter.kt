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
import java.text.SimpleDateFormat
import java.util.Locale

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
        tvNombreCliente.text = "Cliente: ${venta.nombreCliente}"

        // Formatear la fecha
        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()) // Formato de la fecha original
        val outputFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()) // Nuevo formato
        try {
            val date = inputFormat.parse(venta.fechaVenta) // Parsear la fecha original
            val formattedDate = outputFormat.format(date) // Formatear la fecha al nuevo formato
            tvFechaVenta.text = "Fecha: $formattedDate"
        } catch (e: Exception) {
            e.printStackTrace()
            tvFechaVenta.text = "Fecha: Error" // En caso de error de formato
        }

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

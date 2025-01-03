package com.rige.gestiondeudores.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.models.Cliente
import com.rige.gestiondeudores.ui.cliente.ClienteFormActivity
import com.rige.gestiondeudores.ui.cliente.ClienteInfoActivity

class ClienteAdapter(
    private val context: Context,
    private val clientes: List<Cliente>
) : BaseAdapter() {

    override fun getCount(): Int = clientes.size

    override fun getItem(position: Int): Any = clientes[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_cliente, parent, false)

        val cliente = clientes[position]

        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvTelefono = view.findViewById<TextView>(R.id.tvTelefono)
        val btnLlamar = view.findViewById<ImageButton>(R.id.btnLlamar)
        val btnEditar = view.findViewById<ImageButton>(R.id.btnEditar)

        tvNombre.text = cliente.nombre
        tvTelefono.text = cliente.telefono

        btnLlamar.setOnClickListener {
            val telefono = cliente.telefono
            if (telefono.isNullOrBlank() || telefono.length != 9 || !telefono.all { it.isDigit() }) {
                Toast.makeText(context, "Número de teléfono no válido", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefono"))
                context.startActivity(intent)
            }
        }

        btnEditar.setOnClickListener {
            Toast.makeText(context, "Editar cliente: ${cliente.nombre}", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ClienteFormActivity::class.java)
            intent.putExtra("clienteId", cliente.id)
            context.startActivity(intent)
        }

        view.setOnClickListener {
            val intent = Intent(context, ClienteInfoActivity::class.java)
            intent.putExtra("clienteId", cliente.id)
            context.startActivity(intent)
        }

        return view
    }
}
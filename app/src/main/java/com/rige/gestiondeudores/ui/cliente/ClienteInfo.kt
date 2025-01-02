package com.rige.gestiondeudores.ui.cliente

import android.os.Bundle
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.adapters.VentaAdapter
import com.rige.gestiondeudores.database.dao.ClienteDao
import com.rige.gestiondeudores.database.dao.VentaDao

class ClienteInfo : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvDireccion: TextView
    private lateinit var spnFiltro: Spinner
    private lateinit var lvVentas: ListView

    private lateinit var clienteDao: ClienteDao
    private lateinit var ventaDao: VentaDao
    private lateinit var ventaAdapter: VentaAdapter

    private var clienteId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_info)

        initComponents()
        cargarCliente()
    }

    override fun onResume() {
        super.onResume()
        cargarVentas()
    }

    private fun cargarVentas() {
        val ventas = ventaDao.obtenerVentasPorCliente(clienteId)
        ventaAdapter = VentaAdapter(this, ventas)
        lvVentas.adapter = ventaAdapter
    }

    private fun cargarCliente() {
        clienteId = intent.getIntExtra("clienteId", 0)
        val cliente = clienteDao.obtenerClientePorId(clienteId)

        tvNombre.text = "Nombre: ${cliente?.nombre}"
        tvTelefono.text = "Teléfono: ${cliente?.telefono}"
        tvDireccion.text = "Dirección: ${cliente?.direccion}"
    }

    private fun initComponents() {
        tvNombre = findViewById(R.id.tvNombre)
        tvTelefono = findViewById(R.id.tvTelefono)
        tvDireccion = findViewById(R.id.tvDireccion)
        spnFiltro = findViewById(R.id.spnFiltro)
        lvVentas = findViewById(R.id.lvVentas)

        clienteDao = ClienteDao(this)
        ventaDao = VentaDao(this)
    }
}
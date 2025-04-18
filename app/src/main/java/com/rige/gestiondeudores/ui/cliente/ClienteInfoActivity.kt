package com.rige.gestiondeudores.ui.cliente

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.adapters.VentaClienteAdapter
import com.rige.gestiondeudores.database.dao.ClienteDao
import com.rige.gestiondeudores.database.dao.VentaDao
import com.rige.gestiondeudores.ui.venta.VentaInfoActivity

class ClienteInfoActivity : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvDireccion: TextView
    private lateinit var spnFiltro: Spinner
    private lateinit var lvVentas: ListView
    private lateinit var tvFiltro: TextView


    private lateinit var clienteDao: ClienteDao
    private lateinit var ventaDao: VentaDao
    private lateinit var ventaAdapter: VentaClienteAdapter

    private var estado: Boolean? = null

    private var clienteId = 0

    private var paginaActual = 0
    private val ventasPorPagina = 20

    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var tvPagina: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_info)

        initComponents()
        cargarCliente()
        initListeners()
        cargarFiltro()
    }

    override fun onResume() {
        super.onResume()
        cargarVentas(estado)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

//    private fun cargarVentas(estado: Boolean?) {
//        val ventas = ventaDao.obtenerVentasPorCliente(clienteId, estado)
//        val onEditarClick: (Int) -> Unit = { ventaId ->
//            mostrarConfirmacion(ventaId)
//        }
//        val onVentaClick: (Int) -> Unit = { ventaId ->
//            abrirDetalleVenta(ventaId)
//        }
//        ventaAdapter = VentaClienteAdapter(this, ventas, onEditarClick, onVentaClick)
//        lvVentas.adapter = ventaAdapter
//    }

    private fun cargarVentas(estado: Boolean?) {
        val offset = paginaActual * ventasPorPagina
        val ventas = ventaDao.obtenerVentasPorClientePaginado(clienteId, estado, ventasPorPagina, offset)

        val onEditarClick: (Int) -> Unit = { ventaId ->
            mostrarConfirmacion(ventaId)
        }
        val onVentaClick: (Int) -> Unit = { ventaId ->
            abrirDetalleVenta(ventaId)
        }

        ventaAdapter = VentaClienteAdapter(this, ventas, onEditarClick, onVentaClick)
        lvVentas.adapter = ventaAdapter

        // actualizar número de página
        tvPagina.text = "Página ${paginaActual + 1}"

        // desactivar botón anterior si estamos en la página 0
        btnAnterior.isEnabled = paginaActual > 0

        // desactivar siguiente si se recibieron menos de las que caben por página
        btnSiguiente.isEnabled = ventas.size == ventasPorPagina
    }



    private fun abrirDetalleVenta(ventaId: Int) {
        val intent = Intent(this, VentaInfoActivity::class.java)
        intent.putExtra("ventaId", ventaId)
        startActivity(intent)
    }

    private fun mostrarConfirmacion(ventaId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas marcar como PAGADO la venta?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            ventaDao.pagarDeuda(ventaId)
            cargarVentas(estado)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun cargarCliente() {
        clienteId = intent.getIntExtra("clienteId", 0)
        val cliente = clienteDao.obtenerClientePorId(clienteId)

        tvNombre.text = "Nombre: ${cliente?.nombre ?: "sin especificar"}"
        tvTelefono.text = "Teléfono: ${cliente?.telefono ?: "sin especificar"}"
        tvDireccion.text = "Dirección: ${cliente?.direccion ?: "sin especificar"}"
    }

    private fun cargarFiltro() {
        val opciones = listOf("Todos", "Pagados", "Sin pagar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, opciones)
        spnFiltro.adapter = adapter
    }

    private fun mostrarVentas(position: Int) {
        paginaActual = 0 // Reiniciar al cambiar el filtro
        estado = when (position) {
            0 -> null
            1 -> true
            2 -> false
            else -> null
        }
        cargarVentas(estado)
    }


    private fun initListeners() {
        spnFiltro.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                mostrarVentas(position)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        tvFiltro.setOnClickListener {
            spnFiltro.performClick()
        }
    }

    private fun initComponents() {
        tvNombre = findViewById(R.id.tvNombre)
        tvTelefono = findViewById(R.id.tvTelefono)
        tvDireccion = findViewById(R.id.tvDireccion)
        spnFiltro = findViewById(R.id.spnFiltro)
        lvVentas = findViewById(R.id.lvVentas)
        tvFiltro = findViewById(R.id.tvFiltro)

        btnAnterior = findViewById(R.id.btnAnterior)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        tvPagina = findViewById(R.id.tvPagina)

        btnAnterior.setOnClickListener {
            if (paginaActual > 0) {
                paginaActual--
                cargarVentas(estado)
            }
        }

        btnSiguiente.setOnClickListener {
            paginaActual++
            cargarVentas(estado)
        }


        clienteDao = ClienteDao(this)
        ventaDao = VentaDao(this)

        supportActionBar?.title = "Información del cliente"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
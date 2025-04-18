package com.rige.gestiondeudores.ui.venta

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.adapters.VentaAdapter
import com.rige.gestiondeudores.database.dao.VentaDao
import com.rige.gestiondeudores.models.custom.VentaCliente

class VentaListActivity : AppCompatActivity() {

    private lateinit var lvVentas: ListView
    private lateinit var spnFiltro: Spinner
    private lateinit var tvFiltro: TextView

    private lateinit var ventaDao: VentaDao
    private lateinit var listaVentas: List<VentaCliente>
    private lateinit var ventaAdapter: VentaAdapter

    private lateinit var btnAnterior: android.widget.Button
    private lateinit var btnSiguiente: android.widget.Button
    private lateinit var tvPagina: TextView

    private var paginaActual = 1
    private val ventasPorPagina = 30

    private var estado: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta_list)

        supportActionBar?.title = "Lista de Ventas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initComponents()
        initListeners()
        cargarFiltro()
    }

//    private fun cargarVentas() {
//        ventaDao = VentaDao(this)
//        listaVentas = ventaDao.obtenerVentasConClientes(estado)
//        val onEditarClick: (VentaCliente) -> Unit = { venta ->
//            mostrarConfirmacion(venta)
//        }
//        val onVentaClick: (Int) -> Unit = { ventaId ->
//            abrirDetalleVenta(ventaId)
//        }
//        ventaAdapter = VentaAdapter(this, listaVentas, onEditarClick, onVentaClick)
//        lvVentas.adapter = ventaAdapter
//    }

    private fun cargarVentas() {
        ventaDao = VentaDao(this)

        val offset = (paginaActual - 1) * ventasPorPagina
        listaVentas = ventaDao.obtenerVentasConClientePaginado(
            estado = estado,
            limit = ventasPorPagina,
            offset = offset
        )

        val onEditarClick: (VentaCliente) -> Unit = { venta -> mostrarConfirmacion(venta) }
        val onVentaClick: (Int) -> Unit = { ventaId -> abrirDetalleVenta(ventaId) }

        ventaAdapter = VentaAdapter(this, listaVentas, onEditarClick, onVentaClick)
        lvVentas.adapter = ventaAdapter

        tvPagina.text = "Página $paginaActual"

        // Deshabilita botón si es primera página
        btnAnterior.isEnabled = paginaActual > 1

        // Deshabilita si no hay más datos
        btnSiguiente.isEnabled = listaVentas.size == ventasPorPagina
    }


    private fun abrirDetalleVenta(ventaId: Int) {
        val intent = Intent(this, VentaInfoActivity::class.java)
        intent.putExtra("ventaId", ventaId)
        startActivity(intent)
    }

    private fun mostrarVentas(position: Int) {
        estado = when (position) {
            0 -> null
            1 -> true
            2 -> false
            else -> null
        }
        cargarVentas()
    }

    private fun cargarFiltro() {
        val opciones = listOf("Todos", "Pagados", "Sin pagar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, opciones)
        spnFiltro.adapter = adapter
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

    private fun mostrarConfirmacion(venta: VentaCliente) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas marcar como PAGADO la venta?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            ventaDao.pagarDeuda(venta.id)
            cargarVentas()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onResume() {
        cargarVentas()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_add -> {
                val intent = Intent(this, VentaFormActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initComponents() {
        lvVentas = findViewById(R.id.lvVentas)
        spnFiltro = findViewById(R.id.spnFiltro)
        tvFiltro = findViewById(R.id.tvFiltro)

        btnAnterior = findViewById(R.id.btnAnterior)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        tvPagina = findViewById(R.id.tvPagina)

        btnAnterior.setOnClickListener {
            if (paginaActual > 1) {
                paginaActual--
                cargarVentas()
            }
        }

        btnSiguiente.setOnClickListener {
            paginaActual++
            cargarVentas()
        }
    }
}
package com.rige.gestiondeudores.ui.venta

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.database.dao.VentaDao
import java.util.Locale

class VentaInfoActivity : AppCompatActivity() {

    private lateinit var tvMonto: TextView
    private lateinit var tvCliente: TextView
    private lateinit var tvFechaVenta: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvFechaPago: TextView

    private lateinit var ventaDao: VentaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta_info)

        initComponents()
        cargarInfoVenta()

        supportActionBar?.title = "Información de venta"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    private fun cargarInfoVenta() {
        val ventaId = intent.getIntExtra("ventaId", 0)
        val ventaCliente = ventaDao.obtenerVentaPorId(ventaId)
        println(ventaCliente)
        tvMonto.text = "Monto: s/. ${String.format(Locale.getDefault(), "%.2f", ventaCliente?.monto)}"
        tvCliente.text = "Cliente: ${ventaCliente?.nombreCliente}"
        tvFechaVenta.text = "Fecha de Venta: ${ventaCliente?.fechaVenta}"
        tvEstado.text = if (ventaCliente?.estado == true) "Estado: PAGADO" else "Estado: EN DEUDA"
        tvDescripcion.text = "Descripción: ${ventaCliente?.descripcion ?: "No disponible"}"
        tvFechaPago.text = "Fecha de Pago: ${ventaCliente?.fechaPago ?: "No pagado"}"

    }

    private fun initComponents() {
        tvMonto = findViewById(R.id.tvMonto)
        tvCliente = findViewById(R.id.tvCliente)
        tvFechaVenta = findViewById(R.id.tvFechaVenta)
        tvEstado = findViewById(R.id.tvEstado)
        tvDescripcion = findViewById(R.id.tvDescripcion)
        tvFechaPago = findViewById(R.id.tvFechaPago)

        ventaDao = VentaDao(this)
    }
}
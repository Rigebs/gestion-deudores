package com.rige.gestiondeudores.ui.venta

import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.database.dao.ClienteDao
import com.rige.gestiondeudores.database.dao.VentaDao
import com.rige.gestiondeudores.models.Venta

class VentaForm : AppCompatActivity() {

    private lateinit var etMonto: EditText
    private lateinit var etFechaVenta: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etFechaPago: EditText
    private lateinit var swEstado: SwitchCompat
    private lateinit var btnGuardar: Button
    private lateinit var actvCliente: AutoCompleteTextView

    private lateinit var ventaDao: VentaDao
    private lateinit var clienteDao: ClienteDao

    private var clienteId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta_form)

        supportActionBar?.title = "Formulario de Venta"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initComponents()
        initListeners()

        ventaDao = VentaDao(this)
        clienteDao = ClienteDao(this)

        cargarClientes()
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

    private fun guardar() {
        val monto = etMonto.text.toString().toDoubleOrNull()
        val fechaVenta = etFechaVenta.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val fechaPago = etFechaPago.text.toString().trim()
        val estado = swEstado.isChecked

        if (monto == null || monto <= 0) {
            Toast.makeText(this, "El monto debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (fechaVenta.isEmpty()) {
            Toast.makeText(this, "La fecha de venta es obligatoria", Toast.LENGTH_SHORT).show()
            return
        }

        if (clienteId == 0) {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show()
            return
        }

        val venta = Venta(
            monto = monto,
            fechaVenta = fechaVenta,
            estado = estado,
            descripcion = descripcion.ifEmpty { null },
            fechaPago = fechaPago.ifEmpty { null },
            clienteId = clienteId
        )
        ventaDao.insertarVenta(venta)
        Toast.makeText(this, "Venta guardada exitosamente", Toast.LENGTH_SHORT).show()


        limpiarCampos()
        finish()
    }

    private fun limpiarCampos() {
        etMonto.text.clear()
        etFechaVenta.text.clear()
        etDescripcion.text.clear()
        etFechaPago.text.clear()
        swEstado.isChecked = false
        actvCliente.text.clear()
    }

    private fun cargarClientes() {
        val clientes = clienteDao.obtenerTodosLosClientes()
        val nombresClientes = clientes.map { it.nombre }

        val adapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nombresClientes)
        actvCliente.setAdapter(adapter)

        actvCliente.setOnItemClickListener { _, _, position, _ ->
            val clienteSeleccionado = clientes[position]
            clienteId = clienteSeleccionado.id ?: 0
        }
    }

    private fun initListeners() {
        btnGuardar.setOnClickListener { guardar() }
    }

    private fun initComponents() {
        etMonto = findViewById(R.id.etMonto)
        etFechaVenta = findViewById(R.id.etFechaVenta)
        etDescripcion = findViewById(R.id.etDescripcion)
        etFechaPago = findViewById(R.id.etFechaPago)
        swEstado = findViewById(R.id.swEstado)
        btnGuardar = findViewById(R.id.btnGuardar)
        actvCliente = findViewById(R.id.actvCliente)
    }
}

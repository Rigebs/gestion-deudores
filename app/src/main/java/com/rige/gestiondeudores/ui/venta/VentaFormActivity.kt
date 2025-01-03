package com.rige.gestiondeudores.ui.venta

import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.adapters.ClienteAutocompleteAdapter
import com.rige.gestiondeudores.database.dao.ClienteDao
import com.rige.gestiondeudores.database.dao.VentaDao
import com.rige.gestiondeudores.models.Venta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VentaFormActivity : AppCompatActivity() {

    private lateinit var etMonto: EditText
    private lateinit var etDescripcion: EditText
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
        val descripcion = etDescripcion.text.toString().trim()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "El monto debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }
        if (clienteId == 0) {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        val venta = Venta(
            monto = monto,
            fechaVenta = currentDate,
            estado = false,
            descripcion = descripcion.ifEmpty { null },
            clienteId = clienteId
        )
        ventaDao.insertarVenta(venta)
        Toast.makeText(this, "Venta guardada exitosamente", Toast.LENGTH_SHORT).show()


        limpiarCampos()
        finish()
    }


    private fun limpiarCampos() {
        etMonto.text.clear()
        etDescripcion.text.clear()
        actvCliente.text.clear()
    }

    private fun cargarClientes() {

        val clientes = clienteDao.obtenerTodosLosClientes()
        val adapter = ClienteAutocompleteAdapter(this, clientes)
        actvCliente.setAdapter(adapter)

        actvCliente.setOnItemClickListener { _, _, position, _ ->
            val clienteSeleccionado = adapter.getItem(position)
            clienteId = clienteSeleccionado.id ?: 0

            println("Cliente seleccionado: ${clienteSeleccionado.nombre}")
            Toast.makeText(this, "Cliente seleccionado: ${clienteSeleccionado.nombre}", Toast.LENGTH_SHORT).show()
        }

    }



    private fun initListeners() {
        btnGuardar.setOnClickListener { guardar() }
    }

    private fun initComponents() {
        etMonto = findViewById(R.id.etMonto)
        etDescripcion = findViewById(R.id.etDescripcion)
        btnGuardar = findViewById(R.id.btnGuardar)
        actvCliente = findViewById(R.id.actvCliente)
    }
}

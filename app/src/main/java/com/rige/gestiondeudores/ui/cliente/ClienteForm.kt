package com.rige.gestiondeudores.ui.cliente

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.database.dao.ClienteDao
import com.rige.gestiondeudores.models.Cliente

class ClienteForm : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDireccion: EditText
    private lateinit var btnGuardar: Button

    private lateinit var clienteDao: ClienteDao

    private var clienteId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_form)

        supportActionBar?.title = "Formulario del cliente"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initComponents()
        initListeners()
        clienteDao = ClienteDao(this)

        clienteId = intent.getIntExtra("clienteId", 0)
        if (clienteId > 0) {
            cargarDatos(clienteId)
        }
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
        val nombre = etNombre.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        if (clienteId > 0) {
            val cliente = Cliente(
                id = clienteId,
                nombre = nombre,
                telefono = telefono.ifEmpty { null },
                direccion = direccion.ifEmpty { null }
            )
            clienteDao.actualizarCliente(cliente)
            Toast.makeText(this, "Cliente actualizado exitosamente", Toast.LENGTH_SHORT).show()
        } else {
            val cliente = Cliente(
                nombre = nombre,
                telefono = telefono.ifEmpty { null },
                direccion = direccion.ifEmpty { null }
            )
            clienteDao.insertarCliente(cliente)
            Toast.makeText(this, "Cliente guardado exitosamente", Toast.LENGTH_SHORT).show()
        }

        limpiarCampos()
        finish()
    }

    private fun cargarDatos(clienteId: Int) {
        val cliente = clienteDao.obtenerClientePorId(clienteId)
        if (cliente != null) {
            etNombre.setText(cliente.nombre)
            etTelefono.setText(cliente.telefono ?: "")
            etDireccion.setText(cliente.direccion ?: "")
            btnGuardar.text = "Actualizar"
        } else {
            Toast.makeText(this, "Error al cargar el cliente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        etNombre.text.clear()
        etTelefono.text.clear()
        etDireccion.text.clear()
    }

    private fun initListeners() {
        btnGuardar.setOnClickListener { guardar() }
    }

    private fun initComponents() {
        etNombre = findViewById(R.id.etNombre)
        etTelefono = findViewById(R.id.etTelefono)
        etDireccion = findViewById(R.id.etDireccion)
        btnGuardar = findViewById(R.id.btnGuardar)
    }
}
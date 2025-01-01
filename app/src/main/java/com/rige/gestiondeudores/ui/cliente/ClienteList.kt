package com.rige.gestiondeudores.ui.cliente

import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.adapters.ClienteAdapter
import com.rige.gestiondeudores.database.dao.ClienteDao
import com.rige.gestiondeudores.models.Cliente

class ClienteList : AppCompatActivity() {

    private lateinit var lvClientes : ListView

    private lateinit var clienteDao: ClienteDao

    private lateinit var listaClientes : List<Cliente>

    private lateinit var clienteAdapter: ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_list)

        supportActionBar?.title = "Lista de clientes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initComponents()
        cargarClientes()
    }

    private fun cargarClientes() {
        clienteDao = ClienteDao(this)
        listaClientes = clienteDao.obtenerTodosLosClientes()
        clienteAdapter = ClienteAdapter(this, listaClientes)
        lvClientes.adapter = clienteAdapter
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

    private fun initComponents() {
        lvClientes = findViewById(R.id.lvClientes)
    }
}
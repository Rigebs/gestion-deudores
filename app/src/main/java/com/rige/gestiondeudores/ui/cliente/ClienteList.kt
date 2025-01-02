package com.rige.gestiondeudores.ui.cliente

import android.content.Intent
import android.os.Bundle
import android.view.Menu
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
    }

    private fun cargarClientes() {
        clienteDao = ClienteDao(this)
        listaClientes = clienteDao.obtenerTodosLosClientes()
        clienteAdapter = ClienteAdapter(this, listaClientes)
        lvClientes.adapter = clienteAdapter
    }

    override fun onResume() {
        cargarClientes()
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
                val intent = Intent(this, ClienteForm::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initComponents() {
        lvClientes = findViewById(R.id.lvClientes)
    }
}
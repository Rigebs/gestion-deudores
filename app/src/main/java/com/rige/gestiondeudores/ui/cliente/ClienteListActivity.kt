package com.rige.gestiondeudores.ui.cliente

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.adapters.ClienteAdapter
import com.rige.gestiondeudores.database.dao.ClienteDao
import com.rige.gestiondeudores.models.Cliente

class ClienteListActivity : AppCompatActivity() {

    private lateinit var lvClientes : ListView
    private lateinit var clienteDao: ClienteDao
    private lateinit var listaClientes : List<Cliente>
    private lateinit var clienteAdapter: ClienteAdapter
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_list)

        supportActionBar?.title = "Lista de clientes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initComponents()
        setupSearchFilter()
    }

    private fun cargarClientes() {
        clienteDao = ClienteDao(this)
        listaClientes = clienteDao.obtenerTodosLosClientes()
        clienteAdapter = ClienteAdapter(this, listaClientes)
        lvClientes.adapter = clienteAdapter
    }

    private fun setupSearchFilter() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList = listaClientes.filter {
                    it.nombre.contains(s.toString(), ignoreCase = true)
                }
                clienteAdapter = ClienteAdapter(this@ClienteListActivity, filteredList)
                lvClientes.adapter = clienteAdapter
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
                val intent = Intent(this, ClienteFormActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initComponents() {
        lvClientes = findViewById(R.id.lvClientes)
        etSearch = findViewById(R.id.etSearch)
    }
}
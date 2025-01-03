package com.rige.gestiondeudores.ui.venta

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.R
import com.rige.gestiondeudores.adapters.VentaAdapter
import com.rige.gestiondeudores.database.dao.VentaDao
import com.rige.gestiondeudores.models.custom.VentaCliente

class VentaListActivity : AppCompatActivity() {

    private lateinit var lvVentas: ListView
    private lateinit var ventaDao: VentaDao
    private lateinit var listaVentas: List<VentaCliente>
    private lateinit var ventaAdapter: VentaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta_list)

        supportActionBar?.title = "Lista de Ventas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initComponents()
    }

    private fun cargarVentas() {
        ventaDao = VentaDao(this)
        listaVentas = ventaDao.obtenerVentasConClientes()
        val onEditarClick: (VentaCliente) -> Unit = { venta ->
            mostrarConfirmacion(venta)
        }
        val onVentaClick: (Int) -> Unit = { ventaId ->
            abrirDetalleVenta(ventaId)
        }
        ventaAdapter = VentaAdapter(this, listaVentas, onEditarClick, onVentaClick)
        lvVentas.adapter = ventaAdapter
    }

    private fun abrirDetalleVenta(ventaId: Int) {
        val intent = Intent(this, VentaInfoActivity::class.java)
        intent.putExtra("ventaId", ventaId)
        startActivity(intent)
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
    }
}
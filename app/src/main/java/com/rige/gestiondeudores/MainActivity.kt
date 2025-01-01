package com.rige.gestiondeudores

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.rige.gestiondeudores.ui.cliente.ClienteForm
import com.rige.gestiondeudores.ui.cliente.ClienteList

class MainActivity : AppCompatActivity() {

    private lateinit var cvClientes : CardView
    private lateinit var cvVentas : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponents()
        initListeners()
    }

    private fun goTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    private fun initListeners() {
        cvClientes.setOnClickListener { goTo(ClienteList::class.java) }
    }


    private fun initComponents() {
        cvClientes = findViewById(R.id.cvClientes)
        cvVentas = findViewById(R.id.cvVentas)
    }
}
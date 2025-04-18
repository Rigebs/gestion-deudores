package com.rige.gestiondeudores

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import com.rige.gestiondeudores.adapters.ClienteAdapter
import com.rige.gestiondeudores.database.dao.VentaDao
import com.rige.gestiondeudores.ui.cliente.ClienteFormActivity
import com.rige.gestiondeudores.ui.cliente.ClienteListActivity
import com.rige.gestiondeudores.ui.extra.PantallaFinalActivity
import com.rige.gestiondeudores.ui.venta.VentaFormActivity
import com.rige.gestiondeudores.ui.venta.VentaListActivity

class MainActivity : AppCompatActivity() {

    private lateinit var cvClientes: CardView
    private lateinit var cvVentas: CardView
    private lateinit var cvRegistrarVenta: CardView
    private lateinit var cvRegistrarCliente: CardView
    private lateinit var lvClientesPendientes: ListView
    private lateinit var tvTotalDeudas: TextView

    private lateinit var ventaDao: VentaDao
    private lateinit var clienteAdapter: ClienteAdapter

    private lateinit var btnActivar: Button
    private lateinit var tvContador: TextView

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("configuracion", MODE_PRIVATE)
        val isBlocked = prefs.getBoolean("isBlocked", false)
        val isAppActivated = prefs.getBoolean("isAppActivated", false) // Verifica si la app está activada

        if (isBlocked && !isAppActivated) { // Solo redirige si la app está bloqueada Y no activada
            val intent = Intent(this, PantallaFinalActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        initComponents()
        initListeners()

        // Si la aplicación está activada, no ejecutamos el temporizador y ocultamos los elementos
        if (isAppActivated) {
            btnActivar.visibility = Button.GONE
            tvContador.visibility = TextView.GONE
        } else {
            iniciarTemporizador() // Solo iniciar el temporizador si no está activada
        }
    }

    override fun onResume() {
        super.onResume()
        cargarInfo()
    }

    private fun goTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    private fun cargarInfo() {
        val totalDeudas = ventaDao.obtenerTotalDeudas()
        val clientes = ventaDao.obtenerClientesConDeudas()

        tvTotalDeudas.text = "Total de deudas: $totalDeudas"
        clienteAdapter = ClienteAdapter(this, clientes)
        lvClientesPendientes.adapter = clienteAdapter
    }

    fun mostrarDialogoActivacion() {
        // Crear un layout inflado con un EditText para que el usuario ingrese el código
        val inflater = LayoutInflater.from(this)
        val vistaDialogo = inflater.inflate(R.layout.activacion_dialog, null)

        val editTextCodigo = vistaDialogo.findViewById<EditText>(R.id.editTextCodigo)

        // Crear el diálogo
        val dialog = AlertDialog.Builder(this)
            .setTitle("Activar la app")
            .setMessage("Introduce el código de activación")
            .setView(vistaDialogo)
            .setPositiveButton("Aceptar") { _, _ ->
                val codigoIngresado = editTextCodigo.text.toString()
                if (codigoIngresado == "1234") {
                    val prefs = getSharedPreferences("configuracion", MODE_PRIVATE)
                    prefs.edit()
                        .putBoolean("isAppActivated", true)
                        .remove("tiempoInicio") // elimina el tiempo de bloqueo
                        .putBoolean("isBlocked", false)
                        .apply()

                    Toast.makeText(this, "¡App activada!", Toast.LENGTH_SHORT).show()
                    println("APP ACTIVADA")

                    // Ocultar botón y contador
                    btnActivar.visibility = Button.GONE
                    tvContador.visibility = TextView.GONE
                } else {
                    Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun iniciarTemporizador() {
        val prefs = getSharedPreferences("configuracion", MODE_PRIVATE)
        val isAppActivated = prefs.getBoolean("isAppActivated", false)
        if (isAppActivated) return

        val tiempoTotal = 2 * 7 * 24 * 60 * 60 * 1000L // 10 segundos para prueba

        if (!prefs.contains("tiempoInicio")) {
            prefs.edit().putLong("tiempoInicio", System.currentTimeMillis()).apply()
        }

        val tiempoInicio = prefs.getLong("tiempoInicio", 0L)
        val tiempoPasado = System.currentTimeMillis() - tiempoInicio

        if (tiempoPasado >= tiempoTotal) {
            prefs.edit().putBoolean("isBlocked", true).apply()
            val intent = Intent(this, PantallaFinalActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val tiempoRestante = tiempoTotal - tiempoPasado

        countDownTimer = object : CountDownTimer(tiempoRestante, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val semanas = millisUntilFinished / (7 * 24 * 60 * 60 * 1000)
                val dias = (millisUntilFinished % (7 * 24 * 60 * 60 * 1000)) / (24 * 60 * 60 * 1000)
                val horas = (millisUntilFinished % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
                val minutos = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000)
                val segundos = (millisUntilFinished % (60 * 1000)) / 1000

                val tiempoRestanteTexto = when {
                    semanas > 0 -> "Tiempo restante: $semanas semanas, $dias días"
                    dias > 0 -> "Tiempo restante: $dias días, $horas horas"
                    horas > 0 -> "Tiempo restante: $horas horas, $minutos minutos"
                    minutos > 0 -> "Tiempo restante: $minutos minutos, $segundos segundos"
                    else -> "Tiempo restante: $segundos segundos"
                }

                tvContador.text = tiempoRestanteTexto
                println(tiempoRestanteTexto)
            }

            override fun onFinish() {
                val stillBlocked = !prefs.getBoolean("isAppActivated", false)
                if (stillBlocked) {
                    println("TERMINO EL TIEMPO!")
                    prefs.edit().putBoolean("isBlocked", true).apply()
                    val intent = Intent(this@MainActivity, PantallaFinalActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }

    private fun initListeners() {
        cvClientes.setOnClickListener { goTo(ClienteListActivity::class.java) }
        cvVentas.setOnClickListener { goTo(VentaListActivity::class.java) }
        cvRegistrarVenta.setOnClickListener { goTo(VentaFormActivity::class.java) }
        cvRegistrarCliente.setOnClickListener { goTo(ClienteFormActivity::class.java) }

        btnActivar.setOnClickListener { mostrarDialogoActivacion() }
    }

    private fun initComponents() {
        cvClientes = findViewById(R.id.cvClientes)
        cvVentas = findViewById(R.id.cvVentas)
        cvRegistrarVenta = findViewById(R.id.cvRegistrarVenta)
        cvRegistrarCliente = findViewById(R.id.cvRegistrarCliente)
        lvClientesPendientes = findViewById(R.id.lvClientesPendientes)
        tvTotalDeudas = findViewById(R.id.tvTotalDeudas)

        btnActivar = findViewById(R.id.btnActivar)
        tvContador = findViewById(R.id.tvContador)

        ventaDao = VentaDao(this)

        supportActionBar?.hide()
    }
}

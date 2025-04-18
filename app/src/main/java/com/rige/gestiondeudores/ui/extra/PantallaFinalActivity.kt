package com.rige.gestiondeudores.ui.extra

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rige.gestiondeudores.MainActivity
import com.rige.gestiondeudores.R

class PantallaFinalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_final)

        val btnFinal = findViewById<Button>(R.id.btnFinal)
        btnFinal.setOnClickListener {
            mostrarDialogoActivacion()
        }
    }

    private fun mostrarDialogoActivacion() {
        val inflater = LayoutInflater.from(this)
        val vistaDialogo = inflater.inflate(R.layout.activacion_dialog, null)
        val editTextCodigo = vistaDialogo.findViewById<EditText>(R.id.editTextCodigo)

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
                        .putBoolean("isBlocked", false)
                        .remove("tiempoInicio")
                        .apply()

                    Toast.makeText(this, "¡App activada!", Toast.LENGTH_SHORT).show()

                    // Volver a la pantalla principal
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }
}
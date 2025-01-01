package com.rige.gestiondeudores.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "gestion_deudas.db"
        const val DATABASE_VERSION = 1

        const val TABLE_CLIENTES = "clientes"
        const val TABLE_VENTAS = "ventas"

        const val COLUMN_CLIENTE_ID = "id"
        const val COLUMN_CLIENTE_NOMBRE = "nombre"
        const val COLUMN_CLIENTE_TELEFONO = "telefono"
        const val COLUMN_CLIENTE_DIRECCION = "direccion"

        const val COLUMN_VENTA_ID = "id"
        const val COLUMN_VENTA_MONTO = "monto"
        const val COLUMN_VENTA_FECHA = "fecha_venta"
        const val COLUMN_ESTADO = "estado" //esta pagado o no TRUE || FALSE
        const val COLUMN_VENTA_DESCRIPCION = "descripcion"
        const val COLUMN_VENTA_FECHA_PAGO = "fecha_pago"
        const val COLUMN_VENTA_CLIENTE_ID = "cliente_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableClientes = """
        CREATE TABLE $TABLE_CLIENTES (
            $COLUMN_CLIENTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_CLIENTE_NOMBRE TEXT NOT NULL,
            $COLUMN_CLIENTE_TELEFONO TEXT,
            $COLUMN_CLIENTE_DIRECCION TEXT
        )
    """.trimIndent()
        db?.execSQL(createTableClientes)

        val createTableVentas = """
        CREATE TABLE $TABLE_VENTAS (
            $COLUMN_VENTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_VENTA_MONTO REAL NOT NULL,
            $COLUMN_VENTA_FECHA TEXT NOT NULL,
            $COLUMN_ESTADO INTEGER NOT NULL, -- 1 para pagado, 0 para pendiente
            $COLUMN_VENTA_DESCRIPCION TEXT,
            $COLUMN_VENTA_FECHA_PAGO TEXT,
            $COLUMN_VENTA_CLIENTE_ID INTEGER NOT NULL,
            FOREIGN KEY ($COLUMN_VENTA_CLIENTE_ID) REFERENCES $TABLE_CLIENTES($COLUMN_CLIENTE_ID)
            ON DELETE CASCADE
        )
    """.trimIndent()
        db?.execSQL(createTableVentas)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}
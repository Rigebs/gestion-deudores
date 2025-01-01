package com.rige.gestiondeudores.database.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.rige.gestiondeudores.database.DatabaseHelper
import com.rige.gestiondeudores.models.Venta

class VentaDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Insertar una venta
    fun insertarVenta(venta: Venta): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_VENTA_MONTO, venta.monto)
            put(DatabaseHelper.COLUMN_VENTA_FECHA, venta.fechaVenta)
            put(DatabaseHelper.COLUMN_ESTADO, if (venta.estado) 1 else 0)
            put(DatabaseHelper.COLUMN_VENTA_DESCRIPCION, venta.descripcion)
            put(DatabaseHelper.COLUMN_VENTA_FECHA_PAGO, venta.fechaPago)
            put(DatabaseHelper.COLUMN_VENTA_CLIENTE_ID, venta.clienteId)
        }
        return db.insert(DatabaseHelper.TABLE_VENTAS, null, values)
    }

    // Obtener todas las ventas de un cliente
    fun obtenerVentasPorCliente(clienteId: Int): List<Venta> {
        val db = dbHelper.readableDatabase
        val ventas = mutableListOf<Venta>()
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_VENTAS,
            null,
            "${DatabaseHelper.COLUMN_VENTA_CLIENTE_ID} = ?",
            arrayOf(clienteId.toString()),
            null, null, "${DatabaseHelper.COLUMN_VENTA_FECHA} DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val venta = Venta(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENTA_ID)),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENTA_MONTO)),
                    fechaVenta = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENTA_FECHA)),
                    estado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO)) == 1,
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENTA_DESCRIPCION)),
                    fechaPago = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENTA_FECHA_PAGO)),
                    clienteId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENTA_CLIENTE_ID))
                )
                ventas.add(venta)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ventas
    }
}
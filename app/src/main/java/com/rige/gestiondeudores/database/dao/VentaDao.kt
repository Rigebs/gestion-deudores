package com.rige.gestiondeudores.database.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.rige.gestiondeudores.database.DatabaseHelper
import com.rige.gestiondeudores.models.Venta
import com.rige.gestiondeudores.models.custom.VentaCliente
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VentaDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

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

    fun obtenerVentasPorCliente(clienteId: Int, estado: Boolean?): List<Venta> {
        val db = dbHelper.readableDatabase
        val ventas = mutableListOf<Venta>()

        val selectionBuilder = StringBuilder("${DatabaseHelper.COLUMN_VENTA_CLIENTE_ID} = ?")
        val selectionArgs = mutableListOf<String>(clienteId.toString())

        if (estado != null) {
            selectionBuilder.append(" AND ${DatabaseHelper.COLUMN_ESTADO} = ?")
            selectionArgs.add(if (estado) "1" else "0")
        }

        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_VENTAS,
            null,
            selectionBuilder.toString(),
            selectionArgs.toTypedArray(),
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

    fun pagarDeuda(ventaId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ESTADO, 1)
            put(DatabaseHelper.COLUMN_VENTA_FECHA_PAGO, currentDate)
        }

        val filasActualizadas = db.update(
            DatabaseHelper.TABLE_VENTAS,
            values,
            "${DatabaseHelper.COLUMN_VENTA_ID} = ?",
            arrayOf(ventaId.toString())
        )

        db.close()
        return filasActualizadas > 0
    }

    fun obtenerVentasConClientes(): List<VentaCliente> {
        val db = dbHelper.readableDatabase
        val ventasConClientes = mutableListOf<VentaCliente>()

        val query = """
        SELECT 
            v.${DatabaseHelper.COLUMN_VENTA_ID} AS ventaId,
            v.${DatabaseHelper.COLUMN_VENTA_MONTO} AS monto,
            v.${DatabaseHelper.COLUMN_VENTA_FECHA} AS fechaVenta,
            v.${DatabaseHelper.COLUMN_ESTADO} AS estado,
            v.${DatabaseHelper.COLUMN_VENTA_DESCRIPCION} AS descripcion,
            v.${DatabaseHelper.COLUMN_VENTA_FECHA_PAGO} AS fechaPago,
            v.${DatabaseHelper.COLUMN_VENTA_CLIENTE_ID} AS clienteId,
            c.${DatabaseHelper.COLUMN_CLIENTE_NOMBRE} AS nombreCliente
        FROM ${DatabaseHelper.TABLE_VENTAS} v
        INNER JOIN ${DatabaseHelper.TABLE_CLIENTES} c
        ON v.${DatabaseHelper.COLUMN_VENTA_CLIENTE_ID} = c.${DatabaseHelper.COLUMN_CLIENTE_ID}
        ORDER BY v.${DatabaseHelper.COLUMN_VENTA_FECHA} DESC
    """

        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val ventaConCliente = VentaCliente(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("ventaId")),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                    fechaVenta = cursor.getString(cursor.getColumnIndexOrThrow("fechaVenta")),
                    estado = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1,
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    fechaPago = cursor.getString(cursor.getColumnIndexOrThrow("fechaPago")),
                    clienteId = cursor.getInt(cursor.getColumnIndexOrThrow("clienteId")),
                    nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombreCliente"))
                )
                ventasConClientes.add(ventaConCliente)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ventasConClientes
    }

    fun obtenerVentaPorId(ventaId: Int): VentaCliente? {
        val db = dbHelper.readableDatabase

        val query = """
        SELECT 
            v.${DatabaseHelper.COLUMN_VENTA_ID} AS ventaId,
            v.${DatabaseHelper.COLUMN_VENTA_MONTO} AS monto,
            v.${DatabaseHelper.COLUMN_VENTA_FECHA} AS fechaVenta,
            v.${DatabaseHelper.COLUMN_ESTADO} AS estado,
            v.${DatabaseHelper.COLUMN_VENTA_DESCRIPCION} AS descripcion,
            v.${DatabaseHelper.COLUMN_VENTA_FECHA_PAGO} AS fechaPago,
            v.${DatabaseHelper.COLUMN_VENTA_CLIENTE_ID} AS clienteId,
            c.${DatabaseHelper.COLUMN_CLIENTE_NOMBRE} AS nombreCliente
        FROM ${DatabaseHelper.TABLE_VENTAS} v
        INNER JOIN ${DatabaseHelper.TABLE_CLIENTES} c
        ON v.${DatabaseHelper.COLUMN_VENTA_CLIENTE_ID} = c.${DatabaseHelper.COLUMN_CLIENTE_ID}
        WHERE v.${DatabaseHelper.COLUMN_VENTA_ID} = ?
        LIMIT 1
    """

        val cursor: Cursor = db.rawQuery(query, arrayOf(ventaId.toString()))
        var ventaCliente: VentaCliente? = null

        if (cursor.moveToFirst()) {
            ventaCliente = VentaCliente(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("ventaId")),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                fechaVenta = cursor.getString(cursor.getColumnIndexOrThrow("fechaVenta")),
                estado = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) == 1,
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                fechaPago = cursor.getString(cursor.getColumnIndexOrThrow("fechaPago")),
                clienteId = cursor.getInt(cursor.getColumnIndexOrThrow("clienteId")),
                nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombreCliente"))
            )
        }
        cursor.close()
        return ventaCliente
    }
}
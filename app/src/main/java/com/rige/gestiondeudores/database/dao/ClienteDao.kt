package com.rige.gestiondeudores.database.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.rige.gestiondeudores.database.DatabaseHelper
import com.rige.gestiondeudores.models.Cliente

class ClienteDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Insertar un cliente
    fun insertarCliente(cliente: Cliente): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CLIENTE_NOMBRE, cliente.nombre)
            put(DatabaseHelper.COLUMN_CLIENTE_TELEFONO, cliente.telefono)
            put(DatabaseHelper.COLUMN_CLIENTE_DIRECCION, cliente.direccion)
        }
        return db.insert(DatabaseHelper.TABLE_CLIENTES, null, values)
    }

    fun obtenerTodosLosClientes(): List<Cliente> {
        val db = dbHelper.readableDatabase
        val clientes = mutableListOf<Cliente>()
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_CLIENTES, null, null, null, null, null,
            "${DatabaseHelper.COLUMN_CLIENTE_NOMBRE} ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val cliente = Cliente(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLIENTE_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLIENTE_NOMBRE)),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLIENTE_TELEFONO)),
                    direccion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLIENTE_DIRECCION))
                )
                clientes.add(cliente)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return clientes
    }

    fun obtenerClientePorId(clienteId: Int): Cliente? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_CLIENTES,
            null,
            "${DatabaseHelper.COLUMN_CLIENTE_ID} = ?",
            arrayOf(clienteId.toString()),
            null,
            null,
            null
        )

        var cliente: Cliente? = null
        if (cursor.moveToFirst()) {
            cliente = Cliente(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLIENTE_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLIENTE_NOMBRE)),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLIENTE_TELEFONO)),
                direccion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLIENTE_DIRECCION))
            )
        }
        cursor.close()
        return cliente
    }

    // Actualizar un cliente
    fun actualizarCliente(cliente: Cliente): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CLIENTE_NOMBRE, cliente.nombre)
            put(DatabaseHelper.COLUMN_CLIENTE_TELEFONO, cliente.telefono)
            put(DatabaseHelper.COLUMN_CLIENTE_DIRECCION, cliente.direccion)
        }
        return db.update(
            DatabaseHelper.TABLE_CLIENTES, values,
            "${DatabaseHelper.COLUMN_CLIENTE_ID} = ?",
            arrayOf(cliente.id.toString())
        )
    }

    // Eliminar un cliente
    fun eliminarCliente(clienteId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_CLIENTES,
            "${DatabaseHelper.COLUMN_CLIENTE_ID} = ?",
            arrayOf(clienteId.toString())
        )
    }
}
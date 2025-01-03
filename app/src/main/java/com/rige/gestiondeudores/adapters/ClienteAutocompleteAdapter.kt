package com.rige.gestiondeudores.adapters

import android.content.Context
import com.rige.gestiondeudores.models.Cliente

import android.widget.ArrayAdapter
import android.widget.Filter

class ClienteAutocompleteAdapter(
    context: Context,
    private val originalData: List<Cliente>
) : ArrayAdapter<Cliente>(context, android.R.layout.simple_dropdown_item_1line, originalData) {

    private var filtrados: List<Cliente> = ArrayList(originalData)

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint.isNullOrEmpty()) {
                results.values = originalData
                results.count = originalData.size
            } else {
                val filtro = originalData.filter {
                    it.nombre.contains(constraint.toString(), ignoreCase = true)
                }

                results.values = filtro
                results.count = filtro.size
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filtrados = (results?.values as? List<*>)?.filterIsInstance<Cliente>() ?: emptyList()
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int {
        return filtrados.size
    }

    override fun getItem(position: Int): Cliente {
        return filtrados[position]
    }

    override fun getFilter(): Filter {
        return filter
    }
}

package com.example.practica6

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practica6.databinding.ActivitylistviewBinding
import com.parse.ParseObject
import com.parse.ParseQuery

class ListViewActivity: AppCompatActivity() {
    private lateinit var b: ActivitylistviewBinding
    private val datos = mutableListOf<Producto>()
    private val datosFiltrados = mutableListOf<Producto>()
    private lateinit var adapter: ArrayAdapter<String>
    private var consultaActual = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitylistviewBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            mutableListOf<String>()
        )
        b.listViewProductos.adapter = adapter


        b.swipeRefreshLayout.setOnRefreshListener {
            cargarProductos()
        }


        b.swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )


        b.listViewProductos.setOnItemClickListener { parent, view, position, id ->
            val producto = datosFiltrados[position]
            Toast.makeText(
                this,
                "Producto: ${producto.nombre}\nPrecio: $${String.format("%.2f", producto.precio)}",
                Toast.LENGTH_SHORT
            ).show()
        }


        configurarBusqueda()

        cargarProductos()
    }


    private fun configurarBusqueda() {

        b.btnLimpiarBusqueda.setOnClickListener {
            b.editTextBuscar.setText("")
            filtrarProductos("")
        }
        b.editTextBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filtrarProductos(s.toString())
            }
        })
        b.editTextBuscar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                filtrarProductos(b.editTextBuscar.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun filtrarProductos(consulta: String) {
        consultaActual = consulta.trim().lowercase()
        datosFiltrados.clear()

        if (consultaActual.isEmpty()) {
            datosFiltrados.addAll(datos)
        } else {
            for (producto in datos) {
                if (producto.nombre.lowercase().contains(consultaActual)) {
                    datosFiltrados.add(producto)
                }
            }
        }
        actualizarUI()
    }

    private fun actualizarUI() {
        val nombres = datosFiltrados.map {
            "${it.nombre}  \$${"%.2f".format(it.precio)}"
        }
        adapter.clear()
        adapter.addAll(nombres)
        adapter.notifyDataSetChanged()
    }

    private fun cargarProductos() {
        val query = ParseQuery.getQuery<ParseObject>("Producto")
        query.orderByAscending("nombre")
        query.findInBackground{ list, e ->
            b.swipeRefreshLayout.isRefreshing = false

            if(e == null && list != null){
                datos.clear()
                for(po in list){
                    val p = Producto(
                        id = po.objectId,
                        nombre = po.getString("nombre") ?: "Sin nombre",
                        precio = po.getNumber("precio")?.toDouble() ?: 0.0,
                        imagenUrl = po.getString("imagenUrl")
                    )
                    datos.add(p)
                }
                filtrarProductos(consultaActual)
            } else {
                Toast.makeText(
                    this,
                    "Error al cargar los productos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
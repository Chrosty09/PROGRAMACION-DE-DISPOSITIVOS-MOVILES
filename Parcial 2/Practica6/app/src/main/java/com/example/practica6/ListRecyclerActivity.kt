package com.example.practica6

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practica6.databinding.ActivityrecyclerBinding
import com.parse.ParseObject
import com.parse.ParseQuery


class ListRecyclerActivity: AppCompatActivity() {
    private lateinit var b: ActivityrecyclerBinding
    private lateinit var adapter: ProductoAdapter
    private val productosList = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityrecyclerBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter = ProductoAdapter(productosList)
        b.recyclerProductos.layoutManager = LinearLayoutManager(this)
        b.recyclerProductos.adapter = adapter
        configurarClickListener()

        b.swipeRefreshLayout.setOnRefreshListener {
            cargarDesdeBack4App()
        }

        b.swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        cargarDesdeBack4App()
    }

    private fun configurarClickListener() {
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })

        b.recyclerProductos.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val childView = rv.findChildViewUnder(e.x, e.y)
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    val position = rv.getChildAdapterPosition(childView)
                    if (position != RecyclerView.NO_POSITION) {
                        val producto = productosList[position]
                        abrirDetalleProducto(producto)
                        return true
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    private fun abrirDetalleProducto(producto: Producto) {
        val intent = Intent(this, DetalleProductoActivity::class.java).apply {
            putExtra(DetalleProductoActivity.EXTRA_NOMBRE, producto.nombre)
            putExtra(DetalleProductoActivity.EXTRA_PRECIO, producto.precio)
            putExtra(DetalleProductoActivity.EXTRA_IMAGEN_URL, producto.imagenUrl)
        }
        startActivity(intent)
    }

    private fun cargarDesdeBack4App(){
        b.swipeRefreshLayout.isRefreshing = true
        val query = ParseQuery.getQuery<ParseObject>("Producto")
        query.orderByAscending("nombre")
        query.findInBackground{ lista, e ->
            b.swipeRefreshLayout.isRefreshing = false

            if(e == null && lista != null){
                val mapeo = lista.map{ po ->
                    Producto(
                        id = po.objectId,
                        nombre = po.getString("nombre")?: "Sin nombre",
                        precio = po.getNumber("precio")?.toDouble() ?: 0.0,
                        imagenUrl = po.getString("imagenUrl")
                    )
                }
                adapter.submitList(mapeo)
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
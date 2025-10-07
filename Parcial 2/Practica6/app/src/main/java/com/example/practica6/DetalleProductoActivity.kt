package com.example.practica6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.practica6.databinding.ActivityDetalleProductoBinding

class DetalleProductoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleProductoBinding

    companion object {
        const val EXTRA_NOMBRE = "extra_nombre"
        const val EXTRA_PRECIO = "extra_precio"
        const val EXTRA_IMAGEN_URL = "extra_imagen_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombre = intent.getStringExtra(EXTRA_NOMBRE) ?: "Sin nombre"
        val precio = intent.getDoubleExtra(EXTRA_PRECIO, 0.0)
        val imagenUrl = intent.getStringExtra(EXTRA_IMAGEN_URL)

        binding.txtNombreDetalle.text = nombre
        binding.txtPrecioDetalle.text = "$" + String.format("%.2f", precio)

        if (!imagenUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(imagenUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(binding.imgProductoDetalle)
        }
    }
}

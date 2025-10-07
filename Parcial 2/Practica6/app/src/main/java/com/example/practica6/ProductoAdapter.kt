package com.example.practica6

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica6.databinding.ItemproductoBinding

class ProductoAdapter(
    private val items: MutableList<Producto>
) : RecyclerView.Adapter<ProductoAdapter.VH>(){
    inner class VH(val b: ItemproductoBinding):
        RecyclerView.ViewHolder(b.root)

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.nombre == newItem.nombre && oldItem.precio == newItem.precio && oldItem.imagenUrl == newItem.imagenUrl
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        val binding = ItemproductoBinding.inflate(
            inf, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.b.txtNombre.text = p.nombre
        holder.b.txtPrecio.text = "$" + String.format("%.2f", p.precio)

        if(!p.imagenUrl.isNullOrBlank()){
            Glide.with(holder.itemView)
                .load(p.imagenUrl)
                .centerCrop()
                .into(holder.b.imgProducto)
        }else{
            holder.b.imgProducto.setImageDrawable(null)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(nuevos: List<Producto>){
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size
            override fun getNewListSize(): Int = nuevos.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].id == nuevos[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = items[oldItemPosition]
                val newItem = nuevos[newItemPosition]
                return oldItem.nombre == newItem.nombre &&
                       oldItem.precio == newItem.precio &&
                       oldItem.imagenUrl == newItem.imagenUrl
            }
        })
        items.clear()
        items.addAll(nuevos)
        diffResult.dispatchUpdatesTo(this)
    }
}
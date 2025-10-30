package com.example.mireproductor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongInfoAdapter(
    private val songs:List<SongInfo>,
    private val onSongClick:(Int) -> Unit
):RecyclerView.Adapter<SongInfoAdapter.SongViewHolder>(){

    class SongViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imgCancion: ImageView = itemView.findViewById(R.id.imgCancion)
        val nomCancion: TextView = itemView.findViewById(R.id.nomCancion)
        val nomAutor: TextView = itemView.findViewById(R.id.nomAutor)
    }

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):SongViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.canciones, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder:SongViewHolder, position:Int){
        val song = songs[position]
        holder.nomCancion.text = song.title
        holder.nomAutor.text = song.artist
        holder.imgCancion.setImageResource(song.artwork)
        holder.itemView.setOnClickListener{
            onSongClick(position)
        }
    }

    override fun getItemCount():Int = songs.size
}
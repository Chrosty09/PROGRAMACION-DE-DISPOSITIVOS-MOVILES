package com.example.practica5

import android.media.browse.MediaBrowser
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.practica5.databinding.ActivityMainBinding
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import android.content.Intent
import android.util.Log

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private var audioPlayer : ExoPlayer? = null
    private var videoPlayer : ExoPlayer? = null
    private var options = listOf("Imagen","Audio","Video","Texto", "PDF")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            options).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(options[position]){
                    "Imagen" -> loadMedia("Imagen")
                    "Audio" -> loadMedia("Audio")
                    "Video" -> loadMedia("Video")
                    "Texto" -> loadMedia("Texto")
                    "PDF" -> loadMedia("PDF")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setStatus(msg: String){ binding.tvStatus.text = msg}

    private fun showOnly(viewToShow: View?){
        Log.d("MainActivityLog", "showOnly: Mostrando vista ${viewToShow?.id}")
        binding.ivImage.visibility = View.GONE
        binding.playerViewVideo.visibility = View.GONE
        binding.scrollText.visibility = View.GONE
        binding.webViewPdf.visibility = View.GONE
        viewToShow?.visibility = View.VISIBLE
    }

    private fun loadMedia(type: String){
        Log.d("MainActivityLog", "loadMedia: Solicitando tipo: $type")
        relasePlayers()
        setStatus("Buscando '$type' en Back4app")

        val query = ParseQuery.getQuery<ParseObject>("BD")
        query.whereEqualTo("type", type)
        query.getFirstInBackground { obj, e ->
            if(e != null || obj == null){
                setStatus("No se encontró nada: ${e?.message ?: "Objeto nulo"}")
                return@getFirstInBackground
            }

            val file = obj.getParseFile("File")

            if(file == null){
                setStatus("Campo File vacío")
                return@getFirstInBackground
            }

            val url = file.url
            Log.d("MainActivityLog", "loadMedia: URL obtenida: $url")
            when(type){
                "Imagen" -> showImage(url)
                "Audio" -> playAudio(url)
                "Video" -> playVideo(url)
                "Texto" -> loadText(file)
                "PDF" -> showPdf (url)
            }
        }
    }

    private fun showPdf(url: String?) {
        Log.d("MainActivityLog", "showPdf: url=$url")
        if(url == null){
            setStatus("URL vacía")
            return
        }
        showOnly(binding.webViewPdf)
        val pdfUrl = "https://docs.google.com/gview?embedded=true&url=$url"
        binding.webViewPdf.settings.javaScriptEnabled = true
        binding.webViewPdf.loadUrl(pdfUrl)
        setStatus("Mostrando PDF")
    }

    private fun showImage(url: String?){
        Log.d("MainActivityLog", "showImage: url=$url")
        if(url == null){
            setStatus("URL vacia")
            return
        }
        showOnly(binding.ivImage)
        Glide.with(this).load(url).into(binding.ivImage)
        setStatus("Imagen cargada")
    }

    private fun playVideo(url: String?){
        Log.d("MainActivityLog", "playVideo: url=$url")
        if(url == null){
            setStatus("URL Vacia.")
            return
        }
        showOnly(binding.playerViewVideo)
        videoPlayer = ExoPlayer.Builder(this).build().also{ p ->
            binding.playerViewVideo.player = p
            p.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
            p.prepare()
            p.playWhenReady = true
        }
        setStatus("Reproduciendo video")
    }

    private fun playAudio(url: String?){
        Log.d("MainActivityLog", "playAudio: url=$url")
        if(url == null){
            setStatus("URL Vacia.")
            return
        }
        showOnly(null)
        audioPlayer = ExoPlayer.Builder(this).build().also { p ->
            p.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
            p.prepare()
            p.playWhenReady = true
        }
        setStatus("Reproduciendo audio")
    }

    private fun relasePlayers(){
        Log.d("MainActivityLog", "relasePlayers: Liberando recursos")
        audioPlayer?.release(); audioPlayer = null
        videoPlayer?.release(); videoPlayer = null
        binding.playerViewVideo.player = null
        binding.webViewPdf.stopLoading()
        binding.webViewPdf.loadUrl("about:blank")
    }

    private fun loadText(file: ParseFile){
        Log.d("MainActivityLog", "loadText: Descargando texto")
        setStatus("Descargando texto")
        file.getDataInBackground { data, e ->
            if(e != null || data == null){
                setStatus("Error al cargar")
                return@getDataInBackground
            }
            Log.d("MainActivityLog", "loadText: Texto cargado correctamente")
            binding.tvText.text = data.toString(Charsets.UTF_8)
            showOnly(binding.scrollText)
            setStatus("Texto cargado")
        }
    }

    override fun onStop() {
        super.onStop()
        audioPlayer?.playWhenReady = false
        videoPlayer?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        relasePlayers()
    }
}
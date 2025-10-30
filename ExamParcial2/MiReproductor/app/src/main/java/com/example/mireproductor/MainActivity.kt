package com.example.mireproductor

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.common.MediaItem
import android.widget.TextView
import androidx.media3.common.Player
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView




class MainActivity : ComponentActivity(){
    private lateinit var playerView:PlayerView
    private lateinit var tituloCancion:TextView
    private lateinit var autor:TextView
    private lateinit var player:ExoPlayer
    private lateinit var recyclerView:RecyclerView
    private lateinit var songAdapter:SongInfoAdapter

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playerview)
        playerView = findViewById(R.id.player_view)
        tituloCancion = findViewById(R.id.tituloCancion)
        autor = findViewById(R.id.autor)
        recyclerView = findViewById(R.id.canciones)
        playerView.useController = false
        setupRecyclerView()
        setupPlayer()
        setupButtons()
    }

    private fun setupRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        songAdapter=SongInfoAdapter(MusicService.songsInfo) {position->
            player.seekTo(position, 0)
            player.play()
        }
        recyclerView.adapter=songAdapter
    }

    private fun setupPlayer(){
        val existingPlayer = MusicService.player
        if (existingPlayer != null){
            player = existingPlayer
            player.addListener(object:Player.Listener{
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int){
                    super.onMediaItemTransition(mediaItem, reason)
                    val currentIndex = player.currentMediaItemIndex
                    MusicService.currentSongIndex = currentIndex
                    if(currentIndex in MusicService.songsInfo.indices) {
                        updateSongInfo(MusicService.songsInfo[currentIndex])
                    }
                    MusicService.updateWidgetFromContext(this@MainActivity)
                }
            })
        }else{
            player = ExoPlayer.Builder(this).build().also{
                val mediaItem = MediaItem.fromUri("android.resource://$packageName/${R.raw.haveyouever}")
                val mediaItem2 = MediaItem.fromUri("android.resource://$packageName/${R.raw.theshade}")
                val mediaItem3 = MediaItem.fromUri("android.resource://$packageName/${R.raw.treeamongshrubs}")
                it.setMediaItem(mediaItem)
                it.addMediaItem(mediaItem2)
                it.addMediaItem(mediaItem3)
                it.prepare()
                MusicService.player=it

                it.addListener(object:Player.Listener{
                    override fun onMediaItemTransition(mediaItem:MediaItem?, reason:Int){
                        super.onMediaItemTransition(mediaItem, reason)
                        val currentIndex = it.currentMediaItemIndex
                        MusicService.currentSongIndex = currentIndex
                        if(currentIndex in MusicService.songsInfo.indices){
                            updateSongInfo(MusicService.songsInfo[currentIndex])
                        }
                        MusicService.updateWidgetFromContext(this@MainActivity)
                    }
                })
            }
        }
        playerView.player = player
        syncWithService()
    }

    private fun syncWithService(){
        if (::player.isInitialized){
            val playerIndex = player.currentMediaItemIndex
            MusicService.currentSongIndex = playerIndex
            if(playerIndex in MusicService.songsInfo.indices){
                updateSongInfo(MusicService.songsInfo[playerIndex])
            }
        }
    }

    override fun onResume(){
        super.onResume()
        if(::player.isInitialized){
            syncWithService()
        }
    }

    private fun setupButtons(){
        val reproducir=findViewById<Button>(R.id.play)
        reproducir.setOnClickListener{
            if(!player.isPlaying){
                if(player.playbackState == ExoPlayer.STATE_IDLE){
                    player.prepare()
                }
                player.play()
            }
        }
        val pausar = findViewById<Button>(R.id.pause)
        pausar.setOnClickListener{
            player.pause()
        }
        val stop = findViewById<Button>(R.id.stop)
        stop.setOnClickListener{
            player.stop()
        }

        val siguiente = findViewById<Button>(R.id.next)
        siguiente.setOnClickListener{
            player.seekToNextMediaItem()
        }

        val anterior = findViewById<Button>(R.id.previous)
        anterior.setOnClickListener{
            player.seekToPreviousMediaItem()
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun updateSongInfo(songInfo: SongInfo){
        tituloCancion.text = songInfo.title
        autor.text = songInfo.artist
        playerView.defaultArtwork = getDrawable(songInfo.artwork)
    }

    override fun onDestroy(){
        super.onDestroy()
        playerView.player = null
    }
}
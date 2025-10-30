package com.example.mireproductor

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MusicService : Service() {

    companion object {
        const val ACTION_PLAY = "com.example.mireproductor.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.mireproductor.ACTION_PAUSE"
        const val ACTION_STOP = "com.example.mireproductor.ACTION_STOP"
        const val ACTION_NEXT = "com.example.mireproductor.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.mireproductor.ACTION_PREVIOUS"
        var player:ExoPlayer? = null
        var isPlaying = false
        val songsInfo = listOf(
            SongInfo("Have You Ever", "mindfreakkk", R.drawable.song1),
            SongInfo("The Shade", "Rex Orange County", R.drawable.song2),
            SongInfo("Tree Among Shrubs", "Men I Trust", R.drawable.song3)
        )

        var currentSongIndex = 0
        fun updateWidgetFromContext(context: Context){
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, PrimerWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            val intent = Intent(context, PrimerWidget::class.java).apply{
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(intent)
        }
    }

    override fun onCreate(){
        super.onCreate()
        if (player == null){
            player = ExoPlayer.Builder(this).build()
            val mediaItem1 = MediaItem.fromUri("android.resource://$packageName/${R.raw.haveyouever}")
            val mediaItem2 = MediaItem.fromUri("android.resource://$packageName/${R.raw.theshade}")
            val mediaItem3 = MediaItem.fromUri("android.resource://$packageName/${R.raw.treeamongshrubs}")
            player?.setMediaItem(mediaItem1)
            player?.addMediaItem(mediaItem2)
            player?.addMediaItem(mediaItem3)
            player?.prepare()

            player?.addListener(object : Player.Listener{
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int){
                    super.onMediaItemTransition(mediaItem, reason)
                    currentSongIndex = player?.currentMediaItemIndex ?: 0
                    updateWidget()
                }
            })
        }
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, PrimerWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        val intent = Intent(this, PrimerWidget::class.java).apply{
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }
        sendBroadcast(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int):Int{
        when(intent?.action){
            ACTION_PLAY->{
                player?.let{
                    if(!it.isPlaying){
                        if(it.playbackState == ExoPlayer.STATE_IDLE){
                            it.prepare()
                        }
                        it.play()
                        isPlaying = true
                    }
                }
            }
            ACTION_PAUSE->{
                player?.let{
                    if(it.isPlaying){
                        it.pause()
                        isPlaying = false
                    }
                }
            }
            ACTION_STOP->{
                player?.let {
                    it.stop()
                    isPlaying = false
                }
            }
            ACTION_NEXT->{
                player?.seekToNextMediaItem()
            }
            ACTION_PREVIOUS->{
                player?.seekToPreviousMediaItem()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder?{
        return null
    }

    override fun onDestroy(){
        player?.release()
        player = null
        isPlaying = false
        super.onDestroy()
    }
}


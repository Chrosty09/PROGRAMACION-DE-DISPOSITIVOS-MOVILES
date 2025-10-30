package com.example.mireproductor

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class PrimerWidget: AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent){
        super.onReceive(context, intent)

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE){
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            if(ids != null){
                for(id in ids){
                    actualizarWidget(context, appWidgetManager, id)
                }
            }
        }
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray){
        for(id in ids){
            actualizarWidget(context, manager, id)
        }
    }

    private fun actualizarWidget(context: Context, manager: AppWidgetManager, appWidgetId: Int){
        val views = RemoteViews(context.packageName, R.layout.primerwidget2)
        val currentIndex = MusicService.currentSongIndex
        val currentSong = if (currentIndex in MusicService.songsInfo.indices){
            MusicService.songsInfo[currentIndex]
        }else{
            MusicService.songsInfo[0]
        }

        views.setTextViewText(R.id.txtCancion, currentSong.title)
        views.setTextViewText(R.id.txtArtista, currentSong.artist)
        views.setImageViewResource(R.id.imgAlbumArt, currentSong.artwork)

        val playIntent = Intent(context, MusicService::class.java).apply {
            action = MusicService.ACTION_PLAY
        }
        val playPendingIntent = PendingIntent.getService(
            context,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btnPlay, playPendingIntent)


        val pauseIntent = Intent(context, MusicService::class.java).apply{
            action = MusicService.ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getService(
            context,
            1,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btnPause, pausePendingIntent)


        val stopIntent = Intent(context, MusicService::class.java).apply{
            action = MusicService.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            2,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btnStop, stopPendingIntent)

        val nextIntent = Intent(context, MusicService::class.java).apply{
            action = MusicService.ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            context,
            3,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btnNext, nextPendingIntent)

        val previousIntent = Intent(context, MusicService::class.java).apply{
            action = MusicService.ACTION_PREVIOUS
        }
        val previousPendingIntent = PendingIntent.getService(
            context,
            4,
            previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btnPrevious, previousPendingIntent)
        manager.updateAppWidget(appWidgetId, views)
    }

    override fun onEnabled(context: Context){
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context){
        super.onDisabled(context)
        val stopIntent = Intent(context, MusicService::class.java)
        context.stopService(stopIntent)
    }
}
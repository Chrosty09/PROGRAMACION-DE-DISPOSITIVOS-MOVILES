package com.example.paint

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paint.databinding.ActivityMainBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paint = binding.paintView

        binding.spTool.onItemSelectedListener = object : AdapterView.
        OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                paint.currentTool = PaintView.Tool.values()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //paleta de color
        binding.btnCBlack.setOnClickListener { applyColor(Color.BLACK) }
        binding.btnCRed.setOnClickListener { applyColor(0xFFF4436.toInt()) }
        binding.btnCGreen.setOnClickListener { applyColor(0xFF4CAF50.toInt()) }
        binding.btnCBlue.setOnClickListener { applyColor(0xFF2196F3.toInt()) }
        binding.btnCYellow.setOnClickListener { applyColor(0xFFFFEB3B.toInt()) }

        //mezcaldor RGB
        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?,
                                           progress: Int,
                                           fromUser: Boolean) {
                val c = Color.rgb(binding.seekR.progress,
                    binding.seekG.progress,
                    binding.seekB.progress)
                applyColor(c)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        binding.seekR.setOnSeekBarChangeListener(listener)
        binding.seekG.setOnSeekBarChangeListener(listener)
        binding.seekB.setOnSeekBarChangeListener(listener)

        //grosor
        binding.seekStroke.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?,
                                           progress: Int,
                                           fromUser: Boolean) {
                paint.strokeWidthPx =
                    progress.coerceAtLeast(1).toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //acciones
        binding.btnUndo.setOnClickListener { paint.undo() }
        binding.btnClear.setOnClickListener { paint.clearAll() }
        binding.btnSave.setOnClickListener { saveDrawing() }
    }

    private fun applyColor(color: Int){
        binding.paintView.currentColor = color
        binding.vColorPreview.setBackgroundColor(color)
    }

    private fun saveDrawing() {
        val bitmap = binding.paintView.exportBitmap()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "Paint_$timestamp.png"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Paint")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }

                Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show()
        }
    }
}
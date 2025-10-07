package com.example.practica6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.practica6.ui.theme.Practica6Theme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.practica6.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){
    private lateinit var  b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnListView.setOnClickListener {
            startActivity(Intent(this, ListViewActivity::class.java))
        }
        b.btnRecycler.setOnClickListener {
            startActivity(Intent(this, ListRecyclerActivity::class.java))
        }

    }
}
package com.example.sshark

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class Respuesta : AppCompatActivity() {

    private lateinit var imagenTiburon : ImageView
    private lateinit var textNombre : TextView
    private lateinit var textProtegido : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_respuesta)

        imagenTiburon = findViewById(R.id.imageView2)
        textNombre = findViewById(R.id.textView)
        textProtegido = findViewById(R.id.textView4)

        textNombre.apply {
            text = intent.getStringExtra("nombre")
        }
        textProtegido.apply {
            text = intent.getStringExtra("protegido")
        }

        val extras = intent.extras
        val byteArray = extras!!.getByteArray("imagen")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        imagenTiburon.setImageBitmap(bmp)
    }
}
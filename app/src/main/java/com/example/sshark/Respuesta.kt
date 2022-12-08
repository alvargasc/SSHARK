package com.example.sshark

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Respuesta : AppCompatActivity() {

    private lateinit var textNombre : TextView
    private lateinit var textProtegido : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_respuesta)


        textNombre = findViewById(R.id.textView)
        textProtegido = findViewById(R.id.textView4)

        textNombre.apply {
            text = intent.getStringExtra("nombre")
        }
        textProtegido.apply {
            text = intent.getStringExtra("protegido")
        }




    }
}
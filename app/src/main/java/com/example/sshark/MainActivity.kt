package com.example.sshark

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.example.sshark.ml.ModeloTiburonPrueba
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.support.image.TensorImage
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var buttonDorsal : ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_principal)

        buttonDorsal = findViewById(R.id.imageButton6)
        buttonDorsal.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                takePicturePreview.launch(null)
            } else {
                requestPermission.launch(Manifest.permission.CAMERA)
            }

            //val intento = Intent(this, Principal::class.java)
            //startActivity(intento)
        }
    }
    //request camera permission
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted->
        if(granted){
            takePicturePreview.launch(null)
        }else {
            Toast.makeText(this, "Permiso denegado!! Intentalo denuevo", Toast.LENGTH_SHORT).show()
        }
    }

    //launch camera and take picture
    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){ bitmap->
        if(bitmap != null){
            outputGenerator(bitmap)
        }
    }

    private fun outputGenerator(bitmap: Bitmap){
        //declearing tensor flow lite model variable
        val model = ModeloTiburonPrueba.newInstance(this)
        //val model = BirdsModel.newInstance(this)

        // converting bitmap into tensor flow image
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val tfimage = TensorImage.fromBitmap(newBitmap)

        //process the image using trained model and sort it in descending order
        val outputs = model.process(tfimage)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }

        //getting result having high probability
        val highProbabilityOutput = outputs[0]

        //setting output text
        //tvOutput.text = highProbabilityOutput.label
        //inputprueba.setText(highProbabilityOutput.label)
        Log.i("TAG", "outputGenerator: $highProbabilityOutput")

        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        var valor = highProbabilityOutput.label
        println("ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo")
        println(valor)
        val stream = ByteArrayOutputStream()
        // Compress the bitmap with JPEG format and specified quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        println(byteArray)

        db.collection("sSharkBase")
            .document(valor.toString())
            .get()
            .addOnSuccessListener { resultado ->
                val intento1 = Intent(this, Respuesta::class.java)
                intento1.putExtra("nombre", resultado["nombre"].toString() );
                intento1.putExtra("protegido", resultado["protegido"].toString());
                intento1.putExtra("veda",resultado["protegido"].toString() );
                intento1.putExtra("imagen",byteArray)
                startActivity(intento1)
            }
            .addOnFailureListener{ exception ->
                val intento2 = Intent(this, Respuesta::class.java)
                intento2.putExtra("nombre", "No se ha podido conectar");
                startActivity(intento2)
            }

    }
}
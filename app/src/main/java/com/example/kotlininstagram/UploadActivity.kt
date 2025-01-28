package com.example.kotlininstagram

import android.Manifest
import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlininstagram.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()



            }
    fun upload(view: View){

    }

    fun selectImage(view: View){

        if (ContextCompat.checkSelfPermission(this, permission.READ_MEDIA_IMAGES )!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.READ_MEDIA_IMAGES)){

                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permssion"){
                    //request perrmission
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }.show()
            } else{
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
        } else{
            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            //Start activity for result
            activityResultLauncher.launch(intentToGallery)
        }

    }
    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult=result.data
              if (intentFromResult !=null)
              {
                 selectedPicture= intentFromResult.data
                  selectedPicture?.let {
                      binding.imageView.setImageURI(it) }
              }
            }
        }
       permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
           if (result){
               //permission granted
               val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               activityResultLauncher.launch(intentToGallery)
           }else{
               //permissıon denied
               Toast.makeText(this@UploadActivity,"Permission needed!",Toast.LENGTH_LONG).show()
           }

       }

    }

}
package view

import android.Manifest
import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlininstagram.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage:   FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()
        auth = Firebase.auth
        firestore=Firebase.firestore
        storage=Firebase.storage




            }
    fun upload(view: View){
        val uuid=UUID.randomUUID()
        val imageName="$uuid.jpg"
        val reference=storage.reference
        val imageReference=reference.child(imageName)
        if(selectedPicture!=null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener{
                //download url-->Firestore
                val uploadPictureReferance=storage.reference.child("images").child(imageName)
                uploadPictureReferance.downloadUrl.addOnSuccessListener {
                    val downloadUrl=it.toString()
                    if (auth.currentUser!=null){
                    val postMap = hashMapOf<String,Any>()
                    postMap.put("downloadUrl",downloadUrl)
                    postMap.put("userEmail",auth.currentUser!!.email!!)
                    postMap.put("comment",binding.commentText.text.toString())
                    postMap.put("date",Timestamp.now())
                    firestore.collection("posts").add(postMap).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
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
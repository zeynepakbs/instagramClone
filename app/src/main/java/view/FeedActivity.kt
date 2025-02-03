package view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.kotlininstagram.R
import com.example.kotlininstagram.databinding.ActivityFeedBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import model.Post

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private  lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var postArrayList:ArrayList<Post>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth
        db=Firebase.firestore
        postArrayList=ArrayList<Post>()
    }



    private fun getData() {
        db.collection( "Posts").addSnapshotListener{ value, error ->
            if (error != null) {
                Toast.makeText( this, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        for (document in documents) {
                            // casting
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            val post=Post(userEmail,comment,downloadUrl)
                            postArrayList.add(post)
                        }
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater= getMenuInflater()
        menuInflater.inflate(R.menu.insta_menu,menu)


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.add_post){
            val intent =Intent(this, UploadActivity::class.java)
            startActivity(intent)
        } else if(item.itemId== R.id.singout){
          auth.signOut()
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



        return super.onOptionsItemSelected(item)
    }

}
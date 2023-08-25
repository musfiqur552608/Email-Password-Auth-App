package com.example.firebaseapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebaseapp.databinding.ActivityPhotoUploadBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class PhotoUploadActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPhotoUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadBtn.setOnClickListener {
            val intent = Intent()
            intent.action=Intent.ACTION_PICK
            intent.type="image/*"
            imageLauncher.launch(intent)
        }
    }
    val imageLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode==Activity.RESULT_OK){
            if (it.data!=null){
                val ref = Firebase.storage.reference.child("photo")
                ref.putFile(it.data!!.data!!).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        binding.imageView.setImageURI(it)
                        Picasso.get().load(it.toString()).into(binding.imageView)
                    }
                }
            }
        }
    }
}
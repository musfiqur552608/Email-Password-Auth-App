package com.example.firebaseapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.MediaController.MediaPlayerControl
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.firebaseapp.databinding.ActivityVideoBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class VideoActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.videoView.isVisible = false
        binding.button.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "video/*"
            videoLauncher.launch(intent)
        }

    }
    val videoLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== Activity.RESULT_OK){
            if (it.data!=null){
                binding.button.isVisible = false
                binding.videoView.isVisible = true
                val mediaController = MediaController(this)
                mediaController.setAnchorView(binding.videoView)
                binding.videoView.setVideoURI(it.data!!.data)
                binding.videoView.setMediaController(mediaController)
                binding.videoView.start()
            }
        }
    }
}
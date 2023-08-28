package com.example.firebaseapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.MediaController
import android.widget.MediaController.MediaPlayerControl
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.firebaseapp.databinding.ActivityVideoBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class VideoActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVideoBinding
    private lateinit var progressDialog:ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.videoView.isVisible = false
        progressDialog = ProgressDialog(this)
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
                progressDialog.setTitle("Uploading...")
                progressDialog.show()
                val ref = Firebase.storage.reference.child("Video/"+System.currentTimeMillis()+"."+getFileType(it.data!!.data))
                ref.putFile(it.data!!.data!!).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        Firebase.database.reference.child("Video").push().setValue(it.toString())
                        progressDialog.dismiss()
                        Toast.makeText(this@VideoActivity,"Video Uploaded", Toast.LENGTH_SHORT).show()
                        binding.button.isVisible = false
                        binding.videoView.isVisible = true
                        val mediaController = MediaController(this)
                        mediaController.setAnchorView(binding.videoView)
                        binding.videoView.setVideoURI(it)
                        binding.videoView.setMediaController(mediaController)
                        binding.videoView.start()
                        binding.videoView.setOnCompletionListener {
                            ref.delete().addOnSuccessListener {
                                Toast.makeText(this@VideoActivity, "Deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                    .addOnProgressListener {
                        val value=(it.bytesTransferred/it.totalByteCount)*100
                        progressDialog.setTitle("Uploaded"+value.toString()+"%")
                    }

            }
        }
    }
    private fun getFileType(data: Uri?): String? {
        val r = contentResolver
        val mimeType = MimeTypeMap.getSingleton()
        return mimeType.getMimeTypeFromExtension(r.getType(data!!))
    }
}
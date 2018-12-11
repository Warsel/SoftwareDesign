package database

import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import models.User
import java.io.File
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.squareup.picasso.Picasso
import com.google.android.gms.tasks.OnSuccessListener



object Connections {

    private var usersConnection = FirebaseDatabase.getInstance().getReference("users")
    private var storageConnection = FirebaseStorage.getInstance().getReference("images")

    fun saveUser(user: User, image: Uri?) {
        usersConnection.child("main_user").setValue(user)

        if (image != null) {
            storageConnection.child("main_user.jpg").putFile(image)
        }
    }

    fun getUser(callUser : (User) -> Unit, callImage : (Uri) -> Unit) {

        usersConnection.child("main_user").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (user != null) {
                    callUser(user)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        val localFile = File.createTempFile("images", "jpg")

        storageConnection.child("main_user.jpg").getFile(localFile)
            .addOnSuccessListener {
                val imageUri = Uri.fromFile(localFile)
                callImage(imageUri)
            }
    }

    init {
        usersConnection.keepSynced(true)
    }
}
package database

import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import models.User
import java.net.URI


class Connections {

    private var database = FirebaseDatabase.getInstance()
    private var storage = FirebaseStorage.getInstance()

    private fun getUsersConnection(): DatabaseReference {
        return database.getReference("users")
    }

    private fun getStorageConnection(): StorageReference {
        return storage.getReference()
    }

    fun saveUser(user: User, image: Uri?) {
        val userRef = getUsersConnection()
        userRef.child("main_user").setValue(user)

        if (image != null) {
            val storageRef = getStorageConnection()
            storageRef.child("images/rivers.jpg").putFile(image)
        }
    }

    fun getUser(call : (User) -> Unit) {
        val userRef = getUsersConnection()

        userRef.child("main_user").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (user != null) {
                    call(user)
                }
            }

        })
    }

}
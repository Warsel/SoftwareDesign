package database

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import models.RssFeedModel
import models.User
import java.io.File


object Connections {

    private var usersConnection = FirebaseDatabase.getInstance().getReference("users")
    private var usersNewsConnection = FirebaseDatabase.getInstance().getReference("users_news")

    private var storageConnection = FirebaseStorage.getInstance().getReference("images")

    fun saveNews(list: List<RssFeedModel>) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return
        usersNewsConnection.child(auth.uid).setValue(list)
    }

    fun getNews(call: (List<RssFeedModel>) -> Unit) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return

        usersNewsConnection.child(auth.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val list = mutableListOf<RssFeedModel>()
                p0.children.forEach {
                        element -> list.add(element.getValue(RssFeedModel::class.java)!!)
                }
                if (list.isNotEmpty()) {
                    call(list)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun saveUser(user: User, image: Uri?) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return

        usersConnection.child(auth.uid).setValue(user)

        if (image != null) {
            storageConnection.child(auth.uid).putFile(image)
        }
    }

    fun getUser(callUser : (User) -> Unit, callImage : (Uri) -> Unit) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return

        usersConnection.child(auth.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (user != null) {
                    callUser(user)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        storageConnection.child(auth.uid).downloadUrl
            .addOnSuccessListener {
                uri -> callImage(uri)
            }
    }

    init {
        usersConnection.keepSynced(true)
    }
}
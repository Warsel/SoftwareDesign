package database

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import models.RssFeedModel
import models.User
import java.io.File
import android.widget.Toast
import walot.softwaredesign.MainActivity
import androidx.annotation.NonNull
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener




object Connections {

    private var usersConnection = FirebaseDatabase.getInstance().getReference("users")
    private var currentNewsConnection = FirebaseDatabase.getInstance().getReference("current_news")
    private var offlineNewsConnection = FirebaseDatabase.getInstance().getReference("offline_news")
    private var usersFeedSource = FirebaseDatabase.getInstance().getReference("users_feed_source")
    private var storageConnection = FirebaseStorage.getInstance().getReference("images")

    fun saveCurrentNews(list: List<RssFeedModel>) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return
        currentNewsConnection.child(auth.uid).setValue(list)
    }

    fun saveOfflineNews(list: List<RssFeedModel>) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return
        offlineNewsConnection.child(auth.uid).setValue(list)
    }

    fun saveFeedSource(feedSource: String) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return
        usersFeedSource.child(auth.uid).setValue(feedSource)
    }

    fun getFeedSource(call: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return
        usersFeedSource.child(auth.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val feedSource = p0.getValue(String::class.java)
                if (feedSource != null && feedSource.isNotEmpty()) {
                    call(feedSource)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun getCurrentNews(call: (List<RssFeedModel>) -> Unit) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return

        currentNewsConnection.child(auth.uid).addListenerForSingleValueEvent(object : ValueEventListener {
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

    fun getOfflineNews(call: (List<RssFeedModel>) -> Unit) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return

        offlineNewsConnection.child(auth.uid).addListenerForSingleValueEvent(object : ValueEventListener {
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

    fun saveUser(user: User, imageUri: Uri) {
        val auth = FirebaseAuth.getInstance().currentUser ?: return

        usersConnection.child(auth.uid).setValue(user)

        if (imageUri != Uri.EMPTY) {
            storageConnection.putFile(imageUri)
                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation storageConnection.child(auth.uid).downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        user.imageUri = downloadUri.toString()
                        usersConnection.child(auth.uid).setValue(user)
                    }
                }
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
package walot.softwaredesign.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import database.Connections
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import models.User
import walot.softwaredesign.EditProfileActivity
import walot.softwaredesign.R

class ProfileFragment : Fragment() {

    private var auth = FirebaseAuth.getInstance()
    private var user: User = User()
    private lateinit var imageUri: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        setHasOptionsMenu(true)

        imageUri = Uri.EMPTY

        view.edit_profile_btn.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            intent.putExtra("name", user.name)
            intent.putExtra("surname", user.surname)
            intent.putExtra("phoneNumber", user.phoneNumber)
            intent.putExtra("imageUri", user.imageUri)
            startActivity(intent)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        user_email_tv.text = auth.currentUser?.email
        Connections.getUser({u -> getUserFromDatabase(u)}, {i -> getUserImageFromStorage(i)})
        activity!!.toolbar.title = getString(R.string.profile)
    }

    private fun getUserFromDatabase(u: User) {
        user = u

        if (user_name_tv != null) {
            user_name_tv.text = user.name
            user_surname_tv.text = user.surname
            user_phone_number_tv.text = user.phoneNumber

            if (user.imageUri != "") {
                Picasso.get()
                    .load(user.imageUri)
                    .resize(1600, 1200)
                    .into(user_image_iv)
            }
        }
    }

    private fun getUserImageFromStorage(uri: Uri) {
        imageUri = uri

        if (user_image_iv != null) {
            Picasso.get()
                .load(imageUri)
                .resize(1600, 1200)
                .into(user_image_iv)
        }
    }
}
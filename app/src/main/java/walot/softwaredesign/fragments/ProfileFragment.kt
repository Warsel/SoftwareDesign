package walot.softwaredesign.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import database.Connections
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import models.User
import walot.softwaredesign.R

class ProfileFragment : Fragment() {

    var user: User = User()
    var imageUri: Uri? = null
    private var auth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.edit_profile_btn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("name", user.name)
            bundle.putString("surname", user.surname)
            bundle.putString("phoneNumber", user.phoneNumber)
            bundle.putString("image", imageUri.toString())

            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_editProfileFragment, bundle)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        user_name_tv.text = getString(R.string.no_information)
        user_surname_tv.text =  getString(R.string.no_information)
        user_phone_number_tv.text =  getString(R.string.no_information)

        auth = FirebaseAuth.getInstance()

        if (auth!!.currentUser != null) {
            user_email_tv.text =  auth!!.currentUser!!.email
        }

        Connections.getUser({u -> getUserFromDatabase(u)}, {i -> getUserImageFromStorage(i)})
    }

    private fun getUserFromDatabase(user: User) {
        this.user = user

        if (user_name_tv != null) {
            user_name_tv.text = this.user.name
            user_surname_tv.text =  this.user.surname
            user_phone_number_tv.text =  this.user.phoneNumber
        }
    }

    private fun getUserImageFromStorage(uri: Uri?) {
        this.imageUri = uri
        user_image_iv.setImageURI(imageUri)
    }
}
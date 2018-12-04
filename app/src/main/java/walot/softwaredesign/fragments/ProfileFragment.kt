package walot.softwaredesign.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import database.Connections
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import models.User
import walot.softwaredesign.R

class ProfileFragment : Fragment() {

    var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.edit_profile_btn.setOnClickListener {
            val name = user_name_tv.text.toString()
            val surname = user_surname_tv.text.toString()
            val phoneNumber = user_phone_number_tv.text.toString()
            val email = user_email_tv.text.toString()

            val bundle = Bundle()
            bundle.putString("name", name)
            bundle.putString("surname", surname)
            bundle.putString("phoneNumber", phoneNumber)
            bundle.putString("email", email)

            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_editProfileFragment, bundle)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        Connections().getUser { u -> getUserFromDatabase(u) }
    }

    private fun getUserFromDatabase(user: User?) {
        this.user = user

        if (user_name_tv != null) {
            user_name_tv.text = user!!.name
            user_surname_tv.text =  user.surname
            user_phone_number_tv.text =  user.phoneNumber
            user_email_tv.text =  user.email
        }
    }
}
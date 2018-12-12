package walot.softwaredesign.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import database.Connections
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import models.User
import walot.softwaredesign.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import android.content.Context.MODE_PRIVATE
import com.google.firebase.auth.FirebaseAuth


class EditProfileFragment : Fragment() {

    private val IMAGE_FROM_GALLERY = 101
    private val IMAGE_FROM_CAMERA = 102
    private var imageUri : Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        view.user_name_et.setText(arguments!!.getString("name"), TextView.BufferType.EDITABLE)
        view.user_surname_et.setText(arguments!!.getString("surname"), TextView.BufferType.EDITABLE)
        view.user_phone_number_et.setText(arguments!!.getString("phoneNumber"), TextView.BufferType.EDITABLE)
        view.user_image_iv.setImageURI(Uri.parse(arguments!!.getString("image")))

        if (imageUri != null) {
            view.user_image_iv.setImageURI(imageUri)
        }

        view.save_profile_btn.setOnClickListener {
            val name = view.user_name_et.text.toString()
            val surname = view.user_surname_et.text.toString()
            val phoneNumber = view.user_phone_number_et.text.toString()
            val image = imageUri

            val user = User(name, surname, phoneNumber)
            Connections.saveUser(user, image)

            activity!!.onBackPressed()
        }

        view.load_image_from_gallery_btn.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, IMAGE_FROM_GALLERY)
        }

        view.load_image_from_camera_btn.setOnClickListener {
            dispatchTakePictureIntent()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        when (requestCode) {
            IMAGE_FROM_GALLERY -> if (resultCode == RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.data
                Picasso.get()
                    .load(selectedImage)
                    .into(user_image_iv)
                imageUri = selectedImage
            }
            IMAGE_FROM_CAMERA -> if (resultCode == RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.extras.get("data") as Bitmap
                Picasso.get()
                    .load(getImageUri(selectedImage))
                    .into(user_image_iv)
                imageUri = getImageUri(selectedImage)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, IMAGE_FROM_CAMERA)
            }
        }
    }

    private fun getImageUri(image: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val localFile = File.createTempFile("images", "jpg")
        val fileOutputStream = FileOutputStream(localFile)

        fileOutputStream.write(bytes.toByteArray())
        fileOutputStream.close()

        return Uri.fromFile(localFile)
    }
}
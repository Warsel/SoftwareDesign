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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import models.User
import walot.softwaredesign.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class EditProfileFragment : Fragment() {

    private val IMAGE_FROM_GALLERY = 101
    private val IMAGE_FROM_CAMERA = 102

    private lateinit var imageUri : Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        setHasOptionsMenu(true)

        view.user_name_et.setText(arguments!!.getString("name"), TextView.BufferType.EDITABLE)
        view.user_surname_et.setText(arguments!!.getString("surname"), TextView.BufferType.EDITABLE)
        view.user_phone_number_et.setText(arguments!!.getString("phoneNumber"), TextView.BufferType.EDITABLE)
        imageUri = Uri.parse(arguments!!.getString("imageUri"))

        if (imageUri != Uri.EMPTY) {
            Picasso.get()
                .load(imageUri)
                .resize(400, 300)
                .into(view.user_image_iv)
        }

        view.save_profile_btn.setOnClickListener {
            val name = view.user_name_et.text.toString()
            val surname = view.user_surname_et.text.toString()
            val phoneNumber = view.user_phone_number_et.text.toString()
            val image = imageUri

            val user = User(name, surname, phoneNumber, image.toString())
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

    override fun onStart() {
        super.onStart()
        activity!!.toolbar.title = getString(R.string.editing)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        when (requestCode) {
            IMAGE_FROM_GALLERY -> if (resultCode == RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.data
                imageUri = selectedImage

                Picasso.get()
                    .load(imageUri)
                    .resize(400, 300)
                    .into(user_image_iv)
            }
            IMAGE_FROM_CAMERA -> if (resultCode == RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.extras.get("data") as Bitmap
                imageUri = getUriFromBitmap(selectedImage)

                Picasso.get()
                    .load(imageUri)
                    .resize(400, 300)
                    .into(user_image_iv)
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

    private fun getUriFromBitmap(image: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val localFile = File.createTempFile("images", "jpg")
        val fileOutputStream = FileOutputStream(localFile)

        fileOutputStream.write(bytes.toByteArray())
        fileOutputStream.close()

        return Uri.fromFile(localFile)
    }
}
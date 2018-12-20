package walot.softwaredesign

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import database.Connections
import kotlinx.android.synthetic.main.activity_edit_profile.*
import models.User
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class EditProfileActivity : AppCompatActivity() {

    private val IMAGE_FROM_GALLERY = 101
    private val IMAGE_FROM_CAMERA = 102

    private lateinit var imageUri : Uri

    private lateinit var name: String
    private lateinit var surname: String
    private lateinit var phoneNumber: String
    private lateinit var lastImageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(edit_profile_toolbar)

        val actionBar = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.editing)

        user_name_et.setText(intent.getStringExtra("name"), TextView.BufferType.EDITABLE)
        user_surname_et.setText(intent.getStringExtra("surname"), TextView.BufferType.EDITABLE)
        user_phone_number_et.setText(intent.getStringExtra("phoneNumber"), TextView.BufferType.EDITABLE)
        imageUri = Uri.parse(intent.getStringExtra("imageUri"))

        name = intent.getStringExtra("name")
        surname = intent.getStringExtra("surname")
        phoneNumber = intent.getStringExtra("phoneNumber")
        lastImageUri = intent.getStringExtra("imageUri")

        if (imageUri != Uri.EMPTY) {
            Picasso.get()
                .load(imageUri)
                .resize(1600, 1200)
                .into(user_image_iv)
        }

        save_profile_btn.setOnClickListener {
            saveUser()
        }

        load_image_from_gallery_btn.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, IMAGE_FROM_GALLERY)
        }

        load_image_from_camera_btn.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (name != user_name_et.text.toString() ||
            surname != user_surname_et.text.toString() ||
            phoneNumber != user_phone_number_et.text.toString() ||
            lastImageUri != imageUri.toString()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.warning_about_saving))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    saveUser()
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                    super.onBackPressed()
                }
                .create()
                .show()
        }
        else {
            super.onBackPressed()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        when (requestCode) {
            IMAGE_FROM_GALLERY -> if (resultCode == RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.data
                imageUri = selectedImage

                Picasso.get()
                    .load(imageUri)
                    .resize(1600, 1200)
                    .into(user_image_iv)
            }
            IMAGE_FROM_CAMERA -> if (resultCode == RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.extras.get("data") as Bitmap
                imageUri = getUriFromBitmap(selectedImage)

                Picasso.get()
                    .load(imageUri)
                    .resize(1600, 1200)
                    .into(user_image_iv)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
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

    private fun saveUser() {
        val name = user_name_et.text.toString()
        val surname = user_surname_et.text.toString()
        val phoneNumber = user_phone_number_et.text.toString()
        val image = imageUri

        val user = User(name, surname, phoneNumber, image.toString())
        Connections.saveUser(user, image)

        this.finish()
    }
}
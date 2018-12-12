package walot.softwaredesign

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_regisrtation.*


class RegistrationActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regisrtation)

        progressBar.isIndeterminate = true

        auth = FirebaseAuth.getInstance()

        sign_in_btn.setOnClickListener {
            val email = email_et.text.toString()
            val password = password_et.text.toString()

            if (validateFields()) {
                error_tv.text = ""
                sign_in_btn.isEnabled = false
                sign_up_btn.isEnabled = false
                progressBar.visibility = ProgressBar.VISIBLE
                signIn(email, password)
            }
        }

        sign_up_btn.setOnClickListener {
            val email = email_et.text.toString()
            val password = password_et.text.toString()

            if (validateFields()) {
                error_tv.text = ""
                sign_in_btn.isEnabled = false
                sign_up_btn.isEnabled = false
                progressBar.visibility = ProgressBar.VISIBLE
                signUp(email, password)
            }
        }

        about_btn.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        this.finish()
    }

    private fun validateFields(): Boolean {
        var isValidate = true

        if (email_et.text.isEmpty()) {
            email_et.error = getString(R.string.enter_email)
            isValidate = false
        }
        if (password_et.text.isEmpty()) {
            password_et.error = getString(R.string.enter_password)
            isValidate = false
        }

        return isValidate
    }

    private fun signIn(email: String, password: String) {
        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    this.finish()
                } else {
                    sign_in_btn.isEnabled = true
                    sign_up_btn.isEnabled = true
                    progressBar.visibility = ProgressBar.GONE
                    error_tv.text = task.exception!!.localizedMessage
                }
            }
    }

    private fun signUp(email: String, password: String) {
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    this.finish()
                } else {
                    sign_in_btn.isEnabled = true
                    sign_up_btn.isEnabled = true
                    progressBar.visibility = ProgressBar.GONE
                    error_tv.text = task.exception!!.localizedMessage
                }

            }
    }
}
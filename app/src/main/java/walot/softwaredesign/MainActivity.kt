package walot.softwaredesign

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 101
    private val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)

    private val projectVersion = BuildConfig.VERSION_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        version_info_tv.text = getString(R.string.version_info, projectVersion)

        get_imei_btn.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.about_permission))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        requestPermissions(permissions, REQUEST_CODE)
                    }
                    .setNegativeButton(getString(R.string.no)) { _, _ -> }
                    .create()
                    .show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        checkPermission()
        super.onStart()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        @RequiresApi(Build.VERSION_CODES.O)
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    imei_info_tv.text = getString(R.string.imei_info, getImei())
                }
                else {
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.set_permission_yourself))
                            .setPositiveButton(getString(R.string.clear)) { _, _ -> }
                            .create()
                            .show()
                    }
                    else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.about_permission_decline))
                            .setPositiveButton(getString(R.string.sure)) { _, _ -> }
                            .setNegativeButton(getString(R.string.not_sure)) { _, _ ->
                                requestPermissions(permissions, requestCode)
                            }
                            .create()
                            .show()
                    }
                }
                return
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPermission() {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei_info_tv.text = getString(R.string.imei_info, getImei())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun getImei() : String {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.imei
    }
}

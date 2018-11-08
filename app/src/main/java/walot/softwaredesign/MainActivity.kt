package walot.softwaredesign

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 101
    private val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)

    private val projectVersion = BuildConfig.VERSION_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        version_info_tv.text = getString(R.string.version_info, projectVersion)

        requestPermissions(permissions, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        @RequiresApi(Build.VERSION_CODES.O)
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    val imei = telephonyManager.imei
                    imei_info_tv.text = getString(R.string.imei_info, imei)
                }
                else {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(getString(R.string.about_permission))
                        .setPositiveButton(getString(R.string.clear)) { _, _ -> }
                        .create()
                        .show()
                }
                return
            }
        }
    }
}

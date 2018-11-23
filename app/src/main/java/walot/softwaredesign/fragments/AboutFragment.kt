package walot.softwaredesign.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_about.view.*
import walot.softwaredesign.BuildConfig
import walot.softwaredesign.R

class AboutFragment : Fragment() {

    private val REQUEST_CODE = 101
    private val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)

    private val projectVersion = BuildConfig.VERSION_NAME

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        view.version_info_tv.text = getString(R.string.version_info, projectVersion)

        view.get_imei_btn.setOnClickListener {
            if (checkSelfPermission(context!!, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                val builder = AlertDialog.Builder(context!!)
                builder.setMessage(getString(R.string.about_permission))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        requestPermissions(permissions, REQUEST_CODE)
                    }
                    .setNegativeButton(getString(R.string.no)) { _, _ -> }
                    .create()
                    .show()
            }
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPermission() {
        if (checkSelfPermission(context!!, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei_info_tv.text = getString(R.string.imei_info, getImei())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun getImei() : String {
        val telephonyManager = activity!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.imei
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
                        val builder = AlertDialog.Builder(context!!)
                        builder.setMessage(getString(R.string.set_permission_yourself))
                            .setPositiveButton(getString(R.string.clear)) { _, _ -> }
                            .create()
                            .show()
                    }
                    else {
                        val builder = AlertDialog.Builder(context!!)
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
}
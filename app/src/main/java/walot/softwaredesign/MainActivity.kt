package walot.softwaredesign

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var navController: NavController? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.profileFragment -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.newsFragment -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.notPageFragment -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sign_out) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.sign_out_warning))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    auth!!.signOut()
                    navController!!.navigate(R.id.registrationActivity)
                }
                .setNegativeButton(getString(R.string.no)) { _, _ -> }
                .create()
                .show()
        }
        NavigationUI.onNavDestinationSelected(item, navController!!)
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        NavigationUI.setupWithNavController(bottom_navigation, navController!!)
    }

    override fun onStart() {
        super.onStart()
        if (auth!!.currentUser == null) {
            navController!!.navigate(R.id.registrationActivity)
        }
    }
}

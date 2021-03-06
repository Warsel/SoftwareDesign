package walot.softwaredesign.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import database.Connections
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import walot.softwaredesign.R

class NotPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_not_page, container, false)
        setHasOptionsMenu(true)

        return view
    }

    override fun onStart() {
        super.onStart()
        activity!!.toolbar.title = getString(R.string.not_page)
    }
}
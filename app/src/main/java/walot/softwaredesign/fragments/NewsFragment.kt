package walot.softwaredesign.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Xml
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import database.Connections
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_news.view.*
import models.RssFeedListAdapter
import models.RssFeedModel
import org.xmlpull.v1.XmlPullParser
import walot.softwaredesign.R
import java.io.InputStream
import java.net.URL
import java.util.*


class NewsFragment : Fragment() {

    private var feedList: List<RssFeedModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        setHasOptionsMenu(true)

        view.recycler_view.layoutManager = LinearLayoutManager(context)

        view.fetch_feed_btn.setOnClickListener {
            FetchFeedTask().execute(null as Void?)
        }

        view.swipe_refresh_layout.setOnRefreshListener {
            FetchFeedTask().execute(null as Void?)
        }

        view.gone_btn.setOnClickListener {
            view.linear_layout.visibility = View.GONE
        }

        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.source_of_rss).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.source_of_rss) {
            when {
                linear_layout.visibility == View.VISIBLE -> linear_layout.visibility = View.GONE
                linear_layout.visibility == View.GONE -> linear_layout.visibility = View.VISIBLE
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        activity!!.toolbar.title = getString(R.string.news)
        Connections.getFeedSource { feedSource -> rss_feed_et.setText(feedSource) }

        when {
            getConnectionStatus() -> {
                Connections.getCurrentNews { list -> getCachedNews(list) }
            }
            else -> {
                Connections.getOfflineNews { list -> getCachedNews(list) }
                Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (feedList != null) {
            Connections.saveCurrentNews(feedList!!.take(50))
            Connections.saveOfflineNews(feedList!!.takeLast(10))
            Connections.saveFeedSource(rss_feed_et.text.toString())
        }
    }

    private fun getCachedNews(list: List<RssFeedModel>) {
        if (feedList == null && list.isNotEmpty() && linear_layout != null) {
            feedList = list
            linear_layout.visibility = View.GONE

            if (resources.getBoolean(R.bool.is_tablet)) {
                recycler_view.layoutManager = GridLayoutManager(context, resources.getInteger(R.integer.col_num))
            }

            recycler_view.adapter = RssFeedListAdapter(activity!!, feedList!!)
        }
    }

    fun parseFeed(inputStream: InputStream): List<RssFeedModel> {
        var title = ""
        var link = ""
        var description = ""
        var pubDate = ""
        var imageUri = ""

        var isItem = false
        val items = ArrayList<RssFeedModel>()

        inputStream.use { input ->
            val xmlPullParser = Xml.newPullParser()
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xmlPullParser.setInput(input, null)

            xmlPullParser.nextTag()

            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                val eventType = xmlPullParser.eventType
                val name = xmlPullParser.name ?: continue

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equals("item", ignoreCase = true)) {
                        isItem = false
                    }
                    continue
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equals("item", ignoreCase = true)) {
                        isItem = true
                        continue
                    }
                }

                var result = ""

                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.text
                    xmlPullParser.nextTag()
                }

                when {
                    name.equals("title", ignoreCase = true) -> title = result
                    name.equals("link", ignoreCase = true) -> link = result
                    name.equals("description", ignoreCase = true) -> description = result
                    name.equals("pubDate", ignoreCase = true) -> pubDate = result
                    name.equals("enclosure", ignoreCase = true) -> imageUri = xmlPullParser.getAttributeValue("", "url")
                }

                if (title.isNotEmpty() && link.isNotEmpty() && description.isNotEmpty() &&
                    pubDate.isNotEmpty() && imageUri.isNotEmpty()) {

                    if (isItem) {
                        val item = RssFeedModel(title, link, description, pubDate, imageUri)
                        items.add(item)
                    }

                    title = ""
                    link = ""
                    description = ""
                    pubDate = ""
                    imageUri = ""
                    isItem = false
                }
            }

            return items
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class FetchFeedTask : AsyncTask<Void, Void, Boolean>() {

        private lateinit var urlLink: String

        override fun onPreExecute() {
            swipe_refresh_layout.isRefreshing = true
            urlLink = rss_feed_et.text.toString()
        }

        override fun doInBackground(vararg voids: Void): Boolean? {
            if (TextUtils.isEmpty(urlLink))
                return false

            try {
                if (!urlLink.startsWith("http://") && !urlLink.startsWith("https://")) {
                    urlLink = "https://$urlLink"
                }

                val url = URL(urlLink)
                val inputStream = url.openConnection().getInputStream()
                feedList = parseFeed(inputStream)

                return true

            } catch (e: Exception) { }

            return false
        }

        override fun onPostExecute(success: Boolean) {
            swipe_refresh_layout.isRefreshing = false

            if (success) {
                linear_layout.visibility = View.GONE

                if (resources.getBoolean(R.bool.is_tablet)) {
                    recycler_view.layoutManager = GridLayoutManager(context, resources.getInteger(R.integer.col_num))
                }

                recycler_view.adapter = RssFeedListAdapter(activity!!, feedList!!)
            } else {
                when {
                    getConnectionStatus() -> Toast.makeText(context, getString(R.string.no_source_for_rss), Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getConnectionStatus() : Boolean {
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork?.isConnected ?: false
    }
}
package walot.softwaredesign.fragments

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_news.view.*
import models.RssFeedListAdapter
import models.RssFeedModel
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import walot.softwaredesign.R
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*


class NewsFragment : Fragment() {

    private var mFeedModelList: List<RssFeedModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        view.recycler_view.layoutManager = LinearLayoutManager(context)

        view.fetch_feed_btn.setOnClickListener {
            FetchFeedTask().execute(null as Void?)
        }

        view.swipe_refresh_layout.setOnRefreshListener {
            FetchFeedTask().execute(null as Void?)
        }

        /*view.floating_action_btn.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            builder.setMessage("Введите url адресс...")
                .setView(view.rss_feed_et)
                .setPositiveButton("Перейти") { _, _ ->

                }
                .create()
                .show()
        }*/

        return view
    }

    @Throws(XmlPullParserException::class, IOException::class)
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
                    name.equals("enclosure", ignoreCase = true) -> {
                        imageUri = xmlPullParser.getAttributeValue("", "url")
                    }
                }

                if (title.isNotEmpty() &&
                    link.isNotEmpty() &&
                    description.isNotEmpty()
                    && pubDate.isNotEmpty() &&
                    imageUri.isNotEmpty()) {

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

        private var urlLink: String? = null

        override fun onPreExecute() {
            swipe_refresh_layout!!.isRefreshing = true
            urlLink = rss_feed_et!!.text.toString()
        }

        override fun doInBackground(vararg voids: Void): Boolean? {
            if (TextUtils.isEmpty(urlLink))
                return false

            try {
                if (!urlLink!!.startsWith("http://") && !urlLink!!.startsWith("https://")) {
                    urlLink = "https://" + urlLink!!
                }

                val url = URL(urlLink)
                val inputStream = url.openConnection().getInputStream()
                mFeedModelList = parseFeed(inputStream)

                return true

            } catch (e: IOException) {

            } catch (e: XmlPullParserException) {

            }

            return false
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(success: Boolean?) {
            swipe_refresh_layout!!.isRefreshing = false

            if (success!!) {
                linear_layout.visibility = View.GONE
                floating_action_btn.visibility = View.VISIBLE
                recycler_view!!.adapter = RssFeedListAdapter(activity!!, mFeedModelList!!)
            } else {
                Toast.makeText(context, getString(R.string.no_source_for_rss), Toast.LENGTH_LONG).show()
            }
        }
    }
}
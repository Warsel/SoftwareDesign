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

        view.recyclerView!!.layoutManager = LinearLayoutManager(context)

        view.fetch_feed_btn!!.setOnClickListener {
            FetchFeedTask().execute(null as Void?)
        }

        view.swipeRefreshLayout!!.setOnRefreshListener {
            FetchFeedTask().execute(null as Void?)
        }

        return view
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parseFeed(inputStream: InputStream): List<RssFeedModel> {
        var title: String? = null
        var link: String? = null
        var description: String? = null
        var pubDate: String? = null

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
                }

                if (title != null && link != null && description != null && pubDate != null) {
                    if (isItem) {
                        val item = RssFeedModel(title!!, link!!, description!!, pubDate!!)
                        items.add(item)
                    }

                    title = null
                    link = null
                    description = null
                    pubDate = null
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
            swipeRefreshLayout!!.isRefreshing = true
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

        override fun onPostExecute(success: Boolean?) {
            swipeRefreshLayout!!.isRefreshing = false

            if (success!!) {
                recyclerView!!.adapter = RssFeedListAdapter(mFeedModelList!!)
            } else {
                Toast.makeText(context, "Enter a valid Rss feed url", Toast.LENGTH_LONG).show()
            }
        }
    }
}
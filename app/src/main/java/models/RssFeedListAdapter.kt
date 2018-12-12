package models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import walot.softwaredesign.R

class RssFeedListAdapter(private val mRssFeedModels: List<RssFeedModel>) :
    RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder>() {

    override fun getItemCount(): Int {
        return mRssFeedModels.size
    }

    class FeedModelViewHolder(val rssFeedView: View) : RecyclerView.ViewHolder(rssFeedView)

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): FeedModelViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_rss_feed, parent, false)
        return FeedModelViewHolder(v)
    }

    override fun onBindViewHolder(holder: FeedModelViewHolder, position: Int) {
        val rssFeedModel = mRssFeedModels[position]
        (holder.rssFeedView.findViewById(R.id.title_tv) as TextView).text = rssFeedModel.title
        (holder.rssFeedView.findViewById(R.id.description_tv) as TextView).text = rssFeedModel.description
        (holder.rssFeedView.findViewById(R.id.link_tv) as TextView).text = rssFeedModel.link
        (holder.rssFeedView.findViewById(R.id.pub_date_tv) as TextView).text = rssFeedModel.pubDate
    }
}
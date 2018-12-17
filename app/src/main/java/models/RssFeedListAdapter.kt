package models

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_rss_feed.view.*
import walot.softwaredesign.R
import walot.softwaredesign.WebPageActivity


class RssFeedListAdapter(private val context: Context, private val mRssFeedModels: List<RssFeedModel>) :
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
        (holder.rssFeedView.title_tv).text = rssFeedModel.title

        if (rssFeedModel.description.contains("img")) {
            (holder.rssFeedView.description_tv).text = rssFeedModel.description.substringAfter("/>").substringBefore("<")
        }
        else {
            (holder.rssFeedView.description_tv).text = rssFeedModel.description
        }

        (holder.rssFeedView.pub_date_tv).text = rssFeedModel.pubDate

        Picasso.get()
            .load(rssFeedModel.imageUri)
            .resize(350, 250)
            .onlyScaleDown()
            .into((holder.rssFeedView.image_iv))

        holder.rssFeedView.setOnClickListener {
            val intent = Intent(context, WebPageActivity::class.java)
            intent.putExtra("url", rssFeedModel.link)
            context.startActivity(intent)
        }
    }
}
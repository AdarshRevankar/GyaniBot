package servify.hackathon.mumbaihacks.gyanibot.customView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import servify.hackathon.mumbaihacks.gyanibot.R

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
class SubjectRecyclerViewAdapter(private val context: Context, private val items: List<Pair<String, Int>>) :
    RecyclerView.Adapter<SubjectRecyclerViewAdapter.CustomViewHolder>() {

    private var listener: ClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return CustomViewHolder(view)
    }

    fun setClickListener(listener: ClickListener?) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.textView.text = items[position].first
        holder.ivSubject.setImageDrawable(AppCompatResources.getDrawable(context, items[position].second))
        holder.clContainer.setOnClickListener { listener?.onClickListener(items[position].first) }
    }

    class CustomViewHolder(view: View) : ViewHolder(view) {
        val textView: TextView
        val clContainer: View
        val ivSubject: ImageView

        init {
            textView = view.findViewById(R.id.tvSubject)
            clContainer = view.findViewById(R.id.clContainer)
            ivSubject = view.findViewById(R.id.ivSubject)
        }
    }
}
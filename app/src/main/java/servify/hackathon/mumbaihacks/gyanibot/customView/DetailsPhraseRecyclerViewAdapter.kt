package servify.hackathon.mumbaihacks.gyanibot.customView

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import servify.hackathon.mumbaihacks.gyanibot.R

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
class DetailsPhraseRecyclerViewAdapter(private val context: Context, private var items: ArrayList<String>?) :
    RecyclerView.Adapter<DetailsPhraseRecyclerViewAdapter.CustomViewHolder>() {

    private var listener: ClickListener? = null
    var currentPhrase = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    fun addAll(items: ArrayList<String>?) {
        if (items != null) {
            if (this.items == null) {
                this.items = ArrayList()
            }
            this.items?.addAll(items)
            notifyDataSetChanged()
        }
    }

    fun clearAll() {
        this.items?.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.textView.text = items?.get(position)
        holder.clContainer.setOnClickListener {
            listener?.onClickListener(
                items?.get(position) ?: ""
            )
        }
    }

    class CustomViewHolder(view: View) : ViewHolder(view) {
        val textView: TextView
        val clContainer: View

        init {
            textView = view.findViewById(R.id.tvSubject)
            clContainer = view.findViewById(R.id.clContainer)
        }
    }
}
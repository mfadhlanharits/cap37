package com.example.capstonebangkit.api

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.capstonebangkit.R
import java.util.*


class UserAdapter(private val clickedItem: (UserResponse?) -> Unit) : RecyclerView.Adapter<UserAdapter.UserAdapterVH>(),
    Filterable {
    private var userResponseList: MutableList<UserResponse?>? = null
    private var userResponseListFull: List<UserResponse?>? = null
    private var context: Context? = null
    fun setData(userResponseList: MutableList<UserResponse?>?) {
        this.userResponseList = userResponseList
        userResponseListFull = ArrayList(userResponseList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapterVH {
        context = parent.context
        return UserAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.row_users, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserAdapterVH, position: Int) {
        val userResponse = userResponseList!![position]
        val kategoris: String? = userResponse?.kategori
        val username: String? = userResponse?.nama_ikm
        Glide.with(context!!)
            .load(userResponse?.image_url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.iv_ikm)
        holder.kategori.text = kategoris
        holder.nama_ikm.text = username
        holder.imageMore.setOnClickListener { clickedItem.invoke(userResponse) }
    }

    override fun getFilter(): Filter {
        return userFilter
    }

    private val userFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<UserResponse?> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(userResponseListFull!!)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                for (item in userResponseListFull!!) {
                    if (item?.nama_ikm!!.toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                    if (item?.kategori!!.toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            userResponseList!!.clear()
            userResponseList!!.addAll(results.values as Collection<UserResponse?>)
            notifyDataSetChanged()
        }
    }

    interface ClickedItem {
        fun ClickedUser(userResponse: UserResponse?)
    }

    override fun getItemCount(): Int {
        return userResponseList!!.size
    }

    inner class UserAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nama_ikm: TextView
        var imageMore: ImageView
        var iv_ikm: ImageView
        var kategori: TextView

        init {
            kategori = itemView.findViewById(R.id.tv_kategori)
            iv_ikm = itemView.findViewById(R.id.iv_user)
            nama_ikm = itemView.findViewById(R.id.tv_detail_namaikm)
            imageMore = itemView.findViewById(R.id.ImageMore)
        }
    }
}





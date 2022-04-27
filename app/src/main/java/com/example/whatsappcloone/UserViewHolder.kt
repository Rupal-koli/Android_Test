package com.example.whatsappcloone

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(user:User) = with(itemView) {

        tvCount.isVisible = false
        tvTime.isVisible = false

        tvTitle.text = user.name
        tvSubtitle.text = user.status

        Picasso.get()
            .load(user.thumbImage)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(profilePic)
    }
}
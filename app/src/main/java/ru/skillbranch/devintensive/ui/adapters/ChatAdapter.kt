package ru.skillbranch.devintensive.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.ui.custom.AvatarImageView

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.SingleViewHolder>() {
    var items: List<ChatItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val convertView = inflater.inflate(R.layout.item_chat_single, parent, false)
        return SingleViewHolder(convertView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class SingleViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView) {
        val iv_avatar = convertView.findViewById<AvatarImageView>(R.id.iv_avatar_single)
        val tv_title = convertView.findViewById<TextView>(R.id.tv_title_single)

        fun bind(item: ChatItem) {
            iv_avatar.setInitials(item.initials)
            tv_title.text = item.shortDescription
        }
    }
}
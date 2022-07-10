package ru.nikitazar.netology_diploma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.databinding.CardUsersHorizontalBinding
import ru.nikitazar.netology_diploma.databinding.CardUsersVerticalBinding
import ru.nikitazar.netology_diploma.dto.User
import ru.nikitazar.netology_diploma.view.loadCircleCrop

class UserVerticalAdapter(
) : ListAdapter<User, UserVerticalViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVerticalViewHolder {
        val binding = CardUsersVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserVerticalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserVerticalViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}

class UserVerticalViewHolder(
    private val binding: CardUsersVerticalBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.apply {
            name.text = user.name
            val avatarUrl = user.avatar ?: ""
            avatar.loadCircleCrop(avatarUrl, R.drawable.ic_empty_avatar)
        }
    }
}

class UserHorizontalAdapter : ListAdapter<User, UserHorizontalViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHorizontalViewHolder {
        val binding = CardUsersHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserHorizontalViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}

class UserHorizontalViewHolder(
    private val binding: CardUsersHorizontalBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.apply {
            name.text = user.name
            val avatarUrl = user.avatar ?: ""
            avatar.loadCircleCrop(avatarUrl, R.drawable.ic_empty_avatar)
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
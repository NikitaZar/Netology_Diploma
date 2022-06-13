package ru.nikitazar.netology_diploma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.nikitazar.netology_diploma.BuildConfig
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.databinding.CardPostBinding
import ru.nikitazar.netology_diploma.dto.Post
import ru.nikitazar.netology_diploma.view.load
import ru.nikitazar.netology_diploma.view.loadCircleCrop

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onFullscreenAttachment(attachmentUrl: String)
}

class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val appAuth: AppAuth
) : PagingDataAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, appAuth)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = getItem(position)
        if (holder is PostViewHolder) {
            post?.let { holder.bind(it) }
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
    private val appAuth: AppAuth
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        val ownedByMe = post.authorId == appAuth.authStateFlow.value.id

        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            likeCnt.text = post.likeOwnerIds.size.toString()
            attachment.isVisible = false

            val avatarUrl = "${BuildConfig.BASE_URL}avatars/${post.authorAvatar}"
            avatar.loadCircleCrop(avatarUrl, R.drawable.ic_empty_avatar)

            post.attachment?.let { postAttachment ->
                val attachmentUrl = "${BuildConfig.BASE_URL}media/${postAttachment.url}"
                attachment.load(attachmentUrl)
                attachment.isVisible = true

                attachment.setOnClickListener {
                    onInteractionListener.onFullscreenAttachment(attachmentUrl)
                }
            }

            menu.isVisible = ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
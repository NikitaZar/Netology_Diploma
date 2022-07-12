package ru.nikitazar.netology_diploma.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.databinding.CardUsersVerticalBinding
import ru.nikitazar.netology_diploma.dto.User
import ru.nikitazar.netology_diploma.view.loadCircleCrop
import kotlin.coroutines.coroutineContext


class UserSpinnerAdapter constructor(
    private val users: List<User>,
    private val context: Context,
) : BaseAdapter() {

    override fun getCount() = users.size

    override fun getItem(item: Int): User = users[item]

    override fun getItemId(item: Int): Long = item.toLong()

    override fun getView(item: Int, view: View?, viewGroup: ViewGroup?): View {

        val rView = when (view == null) {
            true -> LayoutInflater.from(context).inflate(R.layout.card_users_vertical, viewGroup, false)
            else -> view
        }

        val user = users[item]
        val name = rView.findViewById<TextView>(R.id.name)
        val avatar = rView.findViewById<ImageView>(R.id.avatar)

        name.text = user.name
        user.avatar?.let { avatar.loadCircleCrop(it, R.drawable.ic_empty_avatar) }


        return rView
    }
}
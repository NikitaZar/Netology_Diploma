package ru.nikitazar.netology_diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        val tabLayout = binding.tab
        val vPager = binding.vPager
        vPager.adapter = FragmentAdapter(this)
        TabLayoutMediator(tabLayout, vPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = getString(R.string.posts)
                1 -> tab.text = getString(R.string.events)
            }
        }.attach()

        return binding.root
    }
}

class FragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> FeedFragment()
            1 -> EventsListFragment()
            else -> FeedFragment()
        }
}
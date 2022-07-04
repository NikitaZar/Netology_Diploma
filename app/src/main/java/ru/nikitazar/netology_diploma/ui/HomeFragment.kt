package ru.nikitazar.netology_diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.tabs.TabLayout
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(requireView())
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        view.findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
        view.findViewById<TabLayout>(R.id.toolbarTabs).addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    navController.navigate(
                        when (tab.position) {
                            0 -> R.id.feedFragment
                            1 -> R.id.eventsListFragment
                            else -> throw IllegalArgumentException("Unknown tab destination!")
                        }
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }
}
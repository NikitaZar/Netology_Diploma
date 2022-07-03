package ru.nikitazar.netology_diploma.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.adapter.FeedAdapter
import ru.nikitazar.netology_diploma.adapter.OnInteractionListener
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.databinding.FragmentFeedBinding
import ru.nikitazar.netology_diploma.dto.Post
import ru.nikitazar.netology_diploma.ui.EditPostFragment.Companion.textArg
import ru.nikitazar.netology_diploma.viewModel.AuthViewModel
import ru.nikitazar.netology_diploma.viewModel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val authViewModel: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)

    @Inject
    lateinit var appAuth: AppAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view.findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        authViewModel.data.observe(viewLifecycleOwner) {
            if (it.id == 0L) {
                findNavController().navigate(R.id.authFragment)
            }
        }

        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = FeedAdapter(
            object : OnInteractionListener {
                override fun onEdit(post: Post) {
                    postViewModel.edit(post)
                }

                override fun onLike(post: Post) {
                    when (post.likedByMe) {
                        true -> postViewModel.dislikeById(post.id)
                        false -> postViewModel.likeById(post.id)
                    }
                }

                override fun onRemove(post: Post) {
                    postViewModel.removeById(post.id)
                }

                override fun onFullscreenAttachment(attachmentUrl: String) {
                    //TODO
                }
            },
            appAuth
        )

        binding.list.adapter = adapter

        lifecycleScope.launchWhenCreated {
            postViewModel.data.collectLatest(adapter::submitData)
        }

        postViewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            binding.progress.isVisible = dataState.loading

            if (dataState.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok){}.show()
            }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_editPostFragment)
        }

        setFragmentResultListener("reqUpdate") { _, bundle ->
            val reqUpdateNew = bundle.getBoolean("reqUpdateNew")
            if (reqUpdateNew) {
                adapter.refresh()
            }
        }

        binding.fabNewer.setOnClickListener {
            adapter.refresh()
            binding.list.smoothScrollToPosition(0)
            binding.fabNewer.hide()
        }

        postViewModel.edited.observe(viewLifecycleOwner) { post ->
            Log.i("edited", "Feed")
            if (post.id != 0L) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    Bundle().apply { textArg = post.content })
            }
        }

        return binding.root
    }
}
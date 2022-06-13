package ru.nikitazar.netology_diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.adapter.FeedAdapter
import ru.nikitazar.netology_diploma.adapter.OnInteractionListener
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.databinding.FragmentFeedBinding
import ru.nikitazar.netology_diploma.dto.Post
import ru.nikitazar.netology_diploma.viewModel.AuthViewModel
import ru.nikitazar.netology_diploma.viewModel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val authViewModel: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)

    @Inject
    lateinit var appAuth: AppAuth

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
                    //TODO
                }

                override fun onLike(post: Post) {
                    //TODO
                }

                override fun onRemove(post: Post) {
                    //TODO
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

        binding.fab.setOnClickListener {
            //TODO navigate to create
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

        return binding.root
    }
}
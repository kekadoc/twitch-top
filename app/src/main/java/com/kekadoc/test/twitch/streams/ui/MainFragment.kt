package com.kekadoc.test.twitch.streams.ui

import android.app.Application
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kekadoc.test.twitch.streams.databinding.FragmentTopBinding
import com.kekadoc.test.twitch.streams.databinding.GameViewBinding
import com.kekadoc.test.twitch.streams.dpToPx
import com.kekadoc.test.twitch.streams.model.TwitchTopResponseElement
import com.kekadoc.test.twitch.streams.paging.ExamplePagingSource
import com.kekadoc.test.twitch.streams.repository.Repository
import com.kekadoc.test.twitch.streams.storage.LocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragmentViewModel(application: Application) : AndroidViewModel(application) {

    val flow = Pager(
        PagingConfig(pageSize = 20)
    ) {
        ExamplePagingSource(Repository, LocalStorage.getInstance(application))
    }.flow.cachedIn(viewModelScope)

}

class MainFragment : Fragment() {

    companion object {
        private const val TAG: String = "MainFragment-TAG"
    }

    private val viewModel by activityViewModels<MainFragmentViewModel>()
    private lateinit var binding: FragmentTopBinding
    private val adapter = Adapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTopBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            adapter = this@MainFragment.adapter
            addItemDecoration(Decorator(requireContext().dpToPx(4f).toInt()))
        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.flow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.imageError.visibility = View.INVISIBLE
                when(loadStates.refresh) {
                    is LoadState.Loading -> { binding.indicator.show() }
                    !is LoadState.Loading -> { binding.indicator.hide() }
                    is LoadState.Error -> {
                        binding.imageError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private class Decorator(val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.set(space, space, space, space)
        }
    }

    private object ItemDataCallback : DiffUtil.ItemCallback<TwitchTopResponseElement>() {
        override fun areItemsTheSame(oldItem: TwitchTopResponseElement, newItem: TwitchTopResponseElement): Boolean {
            return oldItem.game.id == newItem.game.id
        }
        override fun areContentsTheSame(oldItem: TwitchTopResponseElement, newItem: TwitchTopResponseElement): Boolean {
            return oldItem == newItem
        }
    }
    private class VH(val binding: GameViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(value: TwitchTopResponseElement?) {
            binding.appCompatImageView.load(value?.game?.logo?.small)
            binding.textViewName.text = value?.game?.name
            binding.textViewChannels.text = "Channels: ${value?.channels.toString()}"
            binding.textViewViewers.text = "Viewers: ${value?.viewers.toString()}"
        }
    }

    private class Adapter : PagingDataAdapter<TwitchTopResponseElement, VH>(ItemDataCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(GameViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(getItem(position))
        }

    }

}
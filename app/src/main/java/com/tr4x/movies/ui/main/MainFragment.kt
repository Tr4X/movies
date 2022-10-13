package com.tr4x.movies.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.tr4x.movies.Injection
import com.tr4x.movies.databinding.FragmentMainBinding
import com.tr4x.movies.ui.MoviesAdapter
import com.tr4x.movies.ui.RemotePresentationState
import com.tr4x.movies.ui.asRemotePresentationState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(
                context = requireContext(),
                owner = this
            )
        )
            .get(MovieViewModel::class.java)

        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )

        return binding.root
    }


    /**
     * Binds the [UiState] provided  by the [SearchRepositoriesViewModel] to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun FragmentMainBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
        uiActions: (UiAction) -> Unit
    ) {
        val moviesAdapter = MoviesAdapter()

        list.adapter = moviesAdapter

        bindList(
            moviesAdapter = moviesAdapter,
            uiState = uiState,
            pagingData = pagingData,
        )
    }

    private fun FragmentMainBinding.bindList(
        moviesAdapter: MoviesAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
    ) {
        retryButton.setOnClickListener { moviesAdapter.retry() }

        val notLoading = moviesAdapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(moviesAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            moviesAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && moviesAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                list.isVisible =
                    loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                retryButton.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && moviesAdapter.itemCount == 0
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
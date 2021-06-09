package com.kekadoc.test.twitch.streams.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kekadoc.test.twitch.streams.model.TwitchTopResponseElement
import com.kekadoc.test.twitch.streams.repository.Repository
import com.kekadoc.test.twitch.streams.repository.LocalStorage


class ExamplePagingSource(private val backend: Repository, private val storage: LocalStorage) : PagingSource<Int, TwitchTopResponseElement>() {

    companion object {
        private const val TAG: String = "Paging-TAG"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TwitchTopResponseElement> {
        try {
            val nextPageNumber = params.key ?: 0
            val from = nextPageNumber * params.loadSize
            val count = params.loadSize

            try {
                val response = backend.load(from = from, count = count)

                val map = mutableMapOf<Int, TwitchTopResponseElement>()
                response.forEachIndexed {index, twitchTopResponseElement ->
                    map[from + index] = twitchTopResponseElement
                }
                storage.saveAll(map)
            } catch (e: Throwable) {}

            val positions = mutableListOf<Int>()
            repeat(count) {
                positions.add(it + from)
            }
            val data = storage.getAll(positions)

            return LoadResult.Page(
                data = data.map { it.value },
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            Log.e(TAG, "load: $e")
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TwitchTopResponseElement>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}
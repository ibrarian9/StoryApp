package com.app.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.storyapp.api.urlData
import com.app.storyapp.models.ListStoryItem
import okio.IOException
import retrofit2.HttpException

class PagingStory(private val apiServce: urlData): PagingSource<Int, ListStoryItem>(){

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val pos = params.key ?: INITIAL_PAGE_INDEX
            val data = apiServce.getAllStory()
            val res = data.body()?.listStory

            LoadResult.Page(
                data = res ?: emptyList(),
                prevKey = if (pos == INITIAL_PAGE_INDEX) null else pos - 1,
                nextKey = if (res!!.isEmpty()) null else pos + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
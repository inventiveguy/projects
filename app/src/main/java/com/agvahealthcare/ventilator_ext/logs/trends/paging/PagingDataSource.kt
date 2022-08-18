package com.agvahealthcare.ventilator_ext.logs.trends.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.agvahealthcare.ventilator_ext.logging.DataLogger
import com.agvahealthcare.ventilator_ext.model.DataStoreModel
import java.lang.Exception

class PagingDataSource(private val dataLogger: DataLogger): PagingSource<Int, ArrayList<DataStoreModel>>() {


    override fun getRefreshKey(state: PagingState<Int, ArrayList<DataStoreModel>>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArrayList<DataStoreModel>> {
        try {
            val nextPageNumber = params.key ?: 1
            val response  = dataLogger.getNewLogs(nextPageNumber)
            Log.d("list",response.toString())

            //val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            return LoadResult.Page(
                data = listOf(response) ,
                prevKey = if (nextPageNumber == 0) null else nextPageNumber - 1,
                nextKey = nextPageNumber.plus(1)
            )
        } catch (e:Exception){
            return LoadResult.Error(e)
        }
    }
}
package com.agvahealthcare.ventilator_ext.logs.trends.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.agvahealthcare.ventilator_ext.logging.DataLogger
import com.agvahealthcare.ventilator_ext.model.DataStoreModel
import java.lang.Exception

class PagingAOtherSource(private val dataLogger: DataLogger) : PagingSource<Int,DataStoreModel>(){
    override fun getRefreshKey(state: PagingState<Int, DataStoreModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataStoreModel> {
        try {
            val nextPageNumber = params.key ?:1
            val response  = dataLogger.getNewLogs(nextPageNumber)
            return LoadResult.Page(
                data=response,
                prevKey = if (nextPageNumber == 0) null else nextPageNumber-1,
                nextKey = nextPageNumber.plus(1)
            )
        } catch (e:Exception){
            return LoadResult.Error(e)
        }
    }

}
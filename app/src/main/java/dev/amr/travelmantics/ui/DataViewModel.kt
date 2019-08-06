package dev.amr.travelmantics.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dev.amr.travelmantics.data.Deal
import dev.amr.travelmantics.data.Result
import dev.amr.travelmantics.data.TravelsRepository
import kotlinx.coroutines.Dispatchers

class DataViewModel : ViewModel() {

    private val coroutineContext = viewModelScope.coroutineContext + Dispatchers.IO
    private var repository: TravelsRepository = TravelsRepository()

    fun loadDeals(): LiveData<Result<List<Deal>>> = liveData(coroutineContext) {
        emit(Result.Loading)
        emit(repository.getDeals())
    }

    fun newDeal(deal: Deal): LiveData<Result<Boolean>> = liveData(coroutineContext) {
        emit(Result.Loading)
        emit(repository.newDeal(deal))
    }
}
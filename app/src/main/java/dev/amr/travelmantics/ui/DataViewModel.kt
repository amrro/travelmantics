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
        // start with loading.
        emit(Result.Loading)

        val result = repository.getDeals()
        // if list is empty, consider it as error to be displayed.
        if (result is Result.Success && result.data.isEmpty())
            emit(Result.Error(Exception("There is no deals at the moment.\n Wait for it.")))
        else
            emit(result)
    }

    fun newDeal(deal: Deal): LiveData<Result<Boolean>> = liveData(coroutineContext) {
        emit(Result.Loading)
        emit(repository.newDeal(deal))
    }
}
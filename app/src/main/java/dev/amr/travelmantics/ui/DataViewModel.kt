package dev.amr.travelmantics.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dev.amr.travelmantics.data.Result
import dev.amr.travelmantics.data.Deal
import dev.amr.travelmantics.data.TravelsRepository

class DataViewModel: ViewModel() {

    // TODO: handle coroutine scope.

    private var repository: TravelsRepository = TravelsRepository()

    fun loadDeals(): LiveData<Result<List<Deal>>> = liveData {
        emit(Result.Loading)
        emit(repository.getDeals())
    }

    fun newDeal(deal: Deal): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        emit(repository.newDeal(deal))
    }
}
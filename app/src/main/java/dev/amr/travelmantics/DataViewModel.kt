package dev.amr.travelmantics

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dev.amr.travelmantics.data.Result
import dev.amr.travelmantics.data.Travel
import dev.amr.travelmantics.data.TravelsRepository

class DataViewModel: ViewModel() {

    // TODO: handle coroutine scope.

    private var repository: TravelsRepository = TravelsRepository()

    fun loadTravels(): LiveData<Result<List<Travel>>> = liveData {
        emit(Result.Loading)
        emit(repository.getTravels())
    }
}
/**
 *                           MIT License
 *
 *                 Copyright (c) 2019 Amr Elghobary
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
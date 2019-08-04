package dev.amr.travelmantics.ui.adddeal

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import dev.amr.travelmantics.util.ObservableViewModel

class DealUIModel : ObservableViewModel() {

    val areInputsReady = MediatorLiveData<Boolean>()

    val title = MutableLiveData("")
    val price = MutableLiveData("")
    val description = MutableLiveData("")
    val imageReady = MutableLiveData(false)

    init {
        areInputsReady.addSource(title) { areInputsReady.value = checkInputs() }
        areInputsReady.addSource(price) { areInputsReady.value = checkInputs() }
        areInputsReady.addSource(description) { areInputsReady.value = checkInputs() }
        areInputsReady.addSource(imageReady) { areInputsReady.value = checkInputs() }
    }

    private fun checkInputs(): Boolean {
        return !(
                title.value.isNullOrEmpty() ||
                        price.value.isNullOrEmpty() ||
                        description.value.isNullOrEmpty() ||
                        imageReady.value == false)
    }
}
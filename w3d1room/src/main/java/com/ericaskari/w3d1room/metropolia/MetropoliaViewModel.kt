package com.ericaskari.w3d1room.metropolia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ericaskari.w3d1room.api.apimodels.President
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MetropoliaViewModel(private val itemsRepository: IMetropoliaRepository) : ViewModel() {
    private val _presidents = MutableStateFlow<List<President>>(emptyList())

    val presidents: Flow<List<President>> = _presidents

    init {
        loadPresidents()
    }


    fun loadPresidents() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = itemsRepository.getPresidents()
            _presidents.value = result
        }
    }
}

package com.ericaskari.w3d1room.wiki

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WikiViewModel(private val itemsRepository: IWikiRepository) : ViewModel() {
    var wikiUiState: Int by mutableStateOf(0)
        private set

    fun getHits(search: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = itemsRepository.getHits(search)
            wikiUiState = result
        }
    }
}

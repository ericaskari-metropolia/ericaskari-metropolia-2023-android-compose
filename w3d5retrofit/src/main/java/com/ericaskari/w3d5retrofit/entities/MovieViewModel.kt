package com.ericaskari.w3d5retrofit.entities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.exercise_5.ui.member.IMovieRepository
import kotlinx.coroutines.flow.Flow

class MovieViewModel(private val itemsRepository: IMovieRepository) : ViewModel() {
    val items: Flow<List<Movie>> = itemsRepository.getAllItemsStream()

}

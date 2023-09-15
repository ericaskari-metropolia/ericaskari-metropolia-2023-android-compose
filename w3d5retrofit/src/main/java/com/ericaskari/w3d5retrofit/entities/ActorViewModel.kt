package com.ericaskari.w3d5retrofit.entities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.exercise_5.ui.member.IActorRepository
import kotlinx.coroutines.flow.Flow

class ActorViewModel(private val itemsRepository: IActorRepository) : ViewModel() {
    val items: Flow<List<Actor>> = itemsRepository.getAllItemsStream()

}

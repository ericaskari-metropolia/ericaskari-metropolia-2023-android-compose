package com.ericaskari.w3d5retrofit.ui

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ericaskari.w3d5retrofit.application.W3D5RetrofitApplication
import com.ericaskari.w3d5retrofit.entities.ActorViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Other Initializers
        // Initializer for ItemEntryViewModel
        initializer {
            ActorViewModel(W3D5RetrofitApplication().actorRepository)
        }
        //...
    }
}
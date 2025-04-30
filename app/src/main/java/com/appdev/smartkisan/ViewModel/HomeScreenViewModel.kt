package com.appdev.smartkisan.ViewModel

import androidx.lifecycle.ViewModel
import com.appdev.smartkisan.Actions.HomeScreenActions
import com.appdev.smartkisan.Repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    fun onAction(homeScreenActions: HomeScreenActions) {
        when (homeScreenActions) {
            else -> {}
        }
    }
}
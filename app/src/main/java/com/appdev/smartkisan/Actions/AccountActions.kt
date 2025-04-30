package com.appdev.smartkisan.Actions

import android.net.Uri

sealed interface AccountActions {
    data object GoBack : AccountActions
    data object GoToProfile : AccountActions
    data object GoToChats : AccountActions
    data object GoToSavedItems : AccountActions
}
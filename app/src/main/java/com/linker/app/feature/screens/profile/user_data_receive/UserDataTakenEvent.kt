package com.linker.app.feature.screens.profile.user_data_receive

sealed interface UserDataTakenEvent {
    data class OnCityNameChange(val cityName: String) : UserDataTakenEvent
    data object OnSubmit : UserDataTakenEvent
}

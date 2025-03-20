package com.example.doancoso3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancoso3.model.UserAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AddressViewModel : ViewModel() {
    private val _selectedAddress = MutableStateFlow<UserAddress?>(null) // Dùng MutableStateFlow thay vì biến thường
    val selectedAddress: StateFlow<UserAddress?> = _selectedAddress.asStateFlow()
    val userName: StateFlow<String?> = selectedAddress.map { it?.name }.stateIn(
        viewModelScope, SharingStarted.Lazily, null
    )
    fun setSelectedAddress(address: UserAddress) {
        _selectedAddress.value = address
    }
}
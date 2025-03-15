package com.example.doancoso3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.doancoso3.model.UserAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddressViewModel : ViewModel() {
    private val _selectedAddress = MutableStateFlow<UserAddress?>(null) // Dùng MutableStateFlow thay vì biến thường
    val selectedAddress: StateFlow<UserAddress?> = _selectedAddress.asStateFlow()

    fun setSelectedAddress(address: UserAddress) {
        _selectedAddress.value = address
    }
}
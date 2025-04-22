package com.example.doancoso3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancoso3.data.UserAddressFirestoreRepository
import com.example.doancoso3.model.UserAddress
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
    
class AddressViewModel : ViewModel() {

    private val repository = UserAddressFirestoreRepository()

    private val _selectedAddress = MutableStateFlow<UserAddress?>(null)
    val selectedAddress: StateFlow<UserAddress?> = _selectedAddress.asStateFlow()

    private val _addresses = MutableStateFlow<List<UserAddress>>(emptyList())
    val addresses: StateFlow<List<UserAddress>> = _addresses.asStateFlow()

    fun setSelectedAddress(address: UserAddress) {
        _selectedAddress.value = address
    }

    fun loadAddresses(userId: String) {
        viewModelScope.launch {
            val list = repository.getUserAddresses(userId)
            _addresses.value = list
            if (list.isNotEmpty()) {
                _selectedAddress.value = list[0] // chọn mặc định địa chỉ đầu tiên
            }
        }
    }

    fun addAddress(userId: String, name: String, address: String, phone: String) {
        viewModelScope.launch {
            repository.addUserAddress(userId, name, address, phone)
            loadAddresses(userId)
        }
    }

    fun updateAddress(userId: String, userAddress: UserAddress) {
        viewModelScope.launch {
            repository.updateUserAddress(userId, userAddress)
            loadAddresses(userId)
        }
    }

    fun deleteAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            repository.deleteUserAddress(userId, addressId)
            loadAddresses(userId)
        }
    }
}

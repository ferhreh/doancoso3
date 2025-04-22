package com.example.doancoso3.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LanguageViewModel : ViewModel() {
    private val _language = MutableStateFlow("vi")
    val language: StateFlow<String> = _language

    fun setLanguage(lang: String) {
        _language.value = lang
    }
}
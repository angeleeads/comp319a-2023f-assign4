package com.example.phonebookapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    private val _contacts = MutableLiveData<List<ContactModel>>()
    val contacts: LiveData<List<ContactModel>> = _contacts

    init {
        viewModelScope.launch {
            _contacts.value = repository.getAllContacts()
        }
    }

    fun insertContact(contact: ContactModel) = viewModelScope.launch {
        repository.insertContact(contact)
        _contacts.value = repository.getAllContacts()
    }

    fun deleteContactById(contactId: Int) = viewModelScope.launch {
        val contact = repository.getContactById(contactId)
        repository.deleteContact(contact)
        _contacts.value = repository.getAllContacts()
    }

    fun getAvatar(avatarKey: String): Call<ResponseBody> {
        return repository.getAvatar(avatarKey)
    }
}
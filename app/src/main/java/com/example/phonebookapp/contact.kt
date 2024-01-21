package com.example.phonebookapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import okhttp3.ResponseBody
import retrofit2.Call

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts WHERE id = :contactId")
    fun getContactById(contactId: Int): ContactModel

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): List<ContactModel>

    @Insert
    fun insertContact(contact: ContactModel)

    @Delete
    fun deleteContact(contact: ContactModel)

    @Query("DELETE FROM contacts")
    fun deleteAllContacts()
}

@Entity(tableName = "contacts")
data class ContactModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val email: String?,
    val address: String?,
    val avatarKey: String
)

class ContactRepository(private val contactDao: ContactDao) {

    fun getContactById(contactId: Int): ContactModel {
        return contactDao.getContactById(contactId)
    }

    fun getAllContacts(): List<ContactModel> {
        return contactDao.getAllContacts()
    }

    fun getAvatar(avatarKey: String): Call<ResponseBody> {
        return RetrofitClient.service.getAvatar(avatarKey)
    }

    fun insertContact(contact: ContactModel) {
        contactDao.insertContact(contact)
    }

    fun deleteContact(contact: ContactModel) {
        contactDao.deleteContact(contact)
    }
}

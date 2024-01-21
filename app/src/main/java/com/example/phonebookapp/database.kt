package com.example.phonebookapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ContactModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}

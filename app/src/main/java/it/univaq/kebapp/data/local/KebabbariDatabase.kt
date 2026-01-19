package it.univaq.kebapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import it.univaq.kebapp.data.local.dao.KebabbariDao
import it.univaq.kebapp.data.local.entity.LocalKebabbari

@Database(entities = [LocalKebabbari::class], version = 2, exportSchema = false )
abstract class KebabbariDatabase : RoomDatabase() {

    abstract fun getKebabbariDao(): KebabbariDao

}
package it.univaq.kebapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import it.univaq.kebapp.data.local.entity.LocalKebabbari
import kotlinx.coroutines.flow.Flow

@Dao
interface KebabbariDao {

    @Upsert
    suspend fun insert(kebabbari: LocalKebabbari)

    @Upsert
    suspend fun insert(kebabbari: List<LocalKebabbari>)

    @Query("SELECT * FROM kebabbari ORDER BY cregione, cprovincia, ccomune")
    fun getKebabbari(): Flow<List<LocalKebabbari>>

    @Query("DELETE FROM kebabbari")
    suspend fun deleteAll()

}
package it.univaq.kebapp.domain.repository

import it.univaq.kebapp.domain.model.Kebabbari
import kotlinx.coroutines.flow.Flow

interface KebabbariLocalRepository {

    suspend fun insert(kebabbari: Kebabbari)

    suspend fun insert(kebabbari: List<Kebabbari>)

    fun getKebabbari(): Flow<List<Kebabbari>>

    suspend fun deleteAll()


}
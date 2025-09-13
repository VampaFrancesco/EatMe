package it.univaq.kebapp.domain.repository

import it.univaq.kebapp.domain.model.Kebabbari

interface KebabbariRemoteRepository {

    suspend fun getKebabbari() : List<Kebabbari>
}
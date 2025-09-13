package it.univaq.kebapp.data.remote

import it.univaq.kebapp.domain.model.Kebabbari
import it.univaq.kebapp.domain.repository.KebabbariRemoteRepository
import javax.inject.Inject

fun RemoteKebabbari.toModel() : Kebabbari = Kebabbari(
    id = null,
    cnome = cnome,
    ccomune = ccomune,
    clatitudine = clatitudine,
    clongitudine = clongitudine,
    cprovincia = cprovincia,
    cregione = cregione
)

data class KebabbariRetrofitRepository @Inject constructor(

    private val kebabbariService: KebabbariService

): KebabbariRemoteRepository {

    override suspend fun getKebabbari(): List<Kebabbari> {

        return kebabbariService.downloadKebabbari().map(RemoteKebabbari::toModel)

    }
}

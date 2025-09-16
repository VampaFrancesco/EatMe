package it.univaq.kebapp.data.local

import it.univaq.kebapp.data.local.dao.KebabbariDao
import it.univaq.kebapp.data.local.entity.LocalKebabbari
import it.univaq.kebapp.domain.model.Kebabbari
import it.univaq.kebapp.domain.repository.KebabbariLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

fun Kebabbari.toLocal(): LocalKebabbari = LocalKebabbari(
    id = id,
    cnome = cnome,
    ccomune = ccomune,
    clatitudine = clatitudine,
    clongitudine = clongitudine,
    cprovincia = cprovincia,
    cregione = cregione
)

fun LocalKebabbari.toModel(): Kebabbari = Kebabbari(
    id = id,
    cnome = cnome,
    ccomune = ccomune,
    clatitudine = clatitudine,
    clongitudine = clongitudine,
    cprovincia = cprovincia,
    cregione = cregione
)

class KebabbariRoomRepository @Inject constructor(
    private val kebabbariDao: KebabbariDao
) : KebabbariLocalRepository{

    override suspend fun insert(kebabbari: Kebabbari) {
        kebabbariDao.insert(kebabbari.toLocal())
    }

    override suspend fun insert(kebabbari: List<Kebabbari>) {
        kebabbariDao.insert(kebabbari.map(Kebabbari::toLocal))
    }

    override fun getKebabbari(): Flow<List<Kebabbari>> {
        return kebabbariDao.getKebabbari().map { list -> list.map(LocalKebabbari::toModel) }
    }

    override suspend fun deleteAll() {
        kebabbariDao.deleteAll()
    }


}

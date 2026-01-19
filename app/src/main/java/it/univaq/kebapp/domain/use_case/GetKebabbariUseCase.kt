package it.univaq.kebapp.domain.use_case

import it.univaq.kebapp.common.Resource
import it.univaq.kebapp.domain.model.Kebabbari
import it.univaq.kebapp.domain.repository.KebabbariLocalRepository
import it.univaq.kebapp.domain.repository.KebabbariRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GetKebabbariUseCase @Inject constructor(
    private val remoteRepo: KebabbariRemoteRepository,
    private val localRepo: KebabbariLocalRepository
) {
    operator fun invoke(): Flow<Resource<List<Kebabbari>>> {
        return flow {
            emit(Resource.Loading("Loading..."))

            localRepo.getKebabbari()
                .catch {
                    emit(Resource.Error("Error, data not found in local database"))
                }
                .collect { list ->
                    if (list.isEmpty()) {
                        try {
                            val data = remoteRepo.getKebabbari()
                            localRepo.insert(data)
                            emit(Resource.Success(data))
                        } catch (e: HttpException) {
                            e.printStackTrace()
                            emit(Resource.Error("HTTP Error: ${e.code()}"))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            emit(Resource.Error("Network error: ${e.message}"))
                        }
                    } else {
                        emit(Resource.Success(list))
                    }
                }
        }
    }
}

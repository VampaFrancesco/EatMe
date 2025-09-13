package it.univaq.kebapp.data.remote

import it.univaq.kebapp.common.API_DATA
import retrofit2.http.GET

interface KebabbariService {

    @GET(API_DATA)
    suspend fun downloadKebabbari(): List<RemoteKebabbari>
}
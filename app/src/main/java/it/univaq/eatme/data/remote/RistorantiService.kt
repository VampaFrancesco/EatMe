package it.univaq.eatme.data.remote

import it.univaq.eatme.common.API_DATA
import retrofit2.http.GET

interface RistorantiService {

    @GET(API_DATA)
    suspend fun downloadRistoranti(): List<RemoteRistoranti>
}
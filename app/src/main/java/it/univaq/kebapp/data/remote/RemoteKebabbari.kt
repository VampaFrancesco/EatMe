package it.univaq.kebapp.data.remote

import com.google.gson.annotations.SerializedName

data class RemoteKebabbari(
    @SerializedName("canno_inserimento")
    val canno_inserimento: String,
    @SerializedName("ccomune")
    val ccomune: String,
    @SerializedName("cdata_e_ora_inserimento")
    val cdata_e_ora_inserimento: String,
    @SerializedName("cidentificatore_in_openstreetmap")
    val cidentificatore_in_openstreetmap: String,
    @SerializedName("clatitudine")
    val clatitudine: Double,
    @SerializedName("clongitudine")
    val clongitudine: Double,
    @SerializedName("cnome")
    val cnome: String,
    @SerializedName("cprovincia")
    val cprovincia: String,
    @SerializedName("cregione")
    val cregione: String
)

//minuto 1:29:32 possibile motivo, url e api non settate correttamete -> usa xampp
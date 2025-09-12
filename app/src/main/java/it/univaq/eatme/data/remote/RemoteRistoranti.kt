package it.univaq.eatme.data.remote

data class RemoteRistoranti(
    val canno_inserimento: String,
    val ccomune: String,
    val cdata_e_ora_inserimento: String,
    val cidentificatore_in_openstreetmap: String,
    val clatitudine: String,
    val clongitudine: String,
    val cnome: String,
    val cprovincia: String,
    val cregione: String
)
package it.univaq.kebapp.data.remote

data class RemoteKebabbari(
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

//minuto 1:29:32 possibile motivo, url e api non settate correttamete -> usa xampp
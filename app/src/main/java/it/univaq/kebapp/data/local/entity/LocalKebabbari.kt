package it.univaq.kebapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kebabbari")
data class LocalKebabbari(

    @PrimaryKey
    val id : Int?,
    val cnome : String,
    val ccomune: String,
    val clatitudine: Double,
    val clongitudine: Double,
    val cprovincia: String,
    val cregione: String
)

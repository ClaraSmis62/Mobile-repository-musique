package fr.eilco.repertoire_musique

import com.google.gson.annotations.SerializedName

class ArtisteModel (
val name : String,
val id : String
)

data class ArtistResponse(
    val artists: List<Artist>
)

data class Artist(
    val id: String,
    val arid : String,
    val name: String,
    @SerializedName("sort-name") val sortName: String,
    val country: String?
)
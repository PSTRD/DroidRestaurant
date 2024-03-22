package fr.isen.estrade.androiderestaurant.model
import com.google.gson.annotations.SerializedName

data class Ingredient(
    val id : String,
    @SerializedName("name_fr") val nameFr: String,
)
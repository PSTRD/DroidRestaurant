package fr.isen.estrade.androiderestaurant.model
import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("name_fr") val nameFr: String,
    val items: List<Dish>
)
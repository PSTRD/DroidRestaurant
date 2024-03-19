package fr.isen.estrade.androiderestaurant.model
import com.google.gson.annotations.SerializedName
import java.net.URL

import java.util.Locale

data class Dish(
    val id: String,
    @SerializedName("name_fr") val nameFr: String,
    val images: List<String>,
    val prices: List<Price>
)
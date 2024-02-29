package fr.isen.bourdier.androiderestaurant.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Category(
    @SerializedName("name_fr") val name: String,
    @SerializedName("items") val items: List<Dish>
) : Serializable
package fr.isen.estrade.androiderestaurant.model

data class CartItem(
    val dishId: String,
    val dishName: String,
    val quantity: Int,
    val unitPrice: Float,
    val totalPrice: Float
)
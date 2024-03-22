package fr.isen.estrade.androiderestaurant.model

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import fr.isen.estrade.androiderestaurant.CartUtil

class CartViewModel : ViewModel() {
    private val _cartItemCount = mutableIntStateOf(0)
    val cartItemCount: State<Int> = _cartItemCount

    fun updateItemCount(context: Context) {
        // Mettre à jour _cartItemCount avec le nouveau nombre d'articles
        _cartItemCount.intValue = CartUtil.countCartItemsLines(context)
    }
}

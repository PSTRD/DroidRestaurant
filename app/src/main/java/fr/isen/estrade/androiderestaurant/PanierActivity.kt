package fr.isen.estrade.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import fr.isen.estrade.androiderestaurant.model.CartItem
import fr.isen.estrade.androiderestaurant.model.TopBar
import fr.isen.estrade.androiderestaurant.ui.theme.AndroidERestaurantTheme
import java.io.File

class PanierActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var cartItems = CartUtil.loadCartItems(this)
        setContent {
            AndroidERestaurantTheme {
                val cartItemsState = remember { mutableStateOf(cartItems) }
                val totalPrice = cartItemsState.value.sumOf { it.totalPrice.toDouble() }
                Scaffold(
                    topBar = {
                        TopBar(
                            title = "Mon Panier",
                            cartItemCount = cartItemsState.value.size,
                            showBackButton = true,
                            onNavigateBack = {
                                finish()
                                //val homeIntent = Intent(this, HomeActivity::class.java)
                                //homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                //this.startActivity(homeIntent)
                            }
                        )
                    },
                    // Utilisez bottomBar pour ajouter un bouton fixe en bas
                    bottomBar = {
                        BottomAppBar { // bottomBar pour un style plus intégré avec l'app bar
                            // Afficher le prix total
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total: ${totalPrice}€", style = MaterialTheme.typography.bodyLarge)

                                Button(
                                    onClick = {
                                        // Logique pour finaliser la commande
                                    }
                                ) {
                                    Text("Commander")
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    PanierScreen(cartItemsState.value, innerPadding) { indexToRemove ->
                        // Suppression de l'élément basée sur l'index
                        cartItems = cartItems.toMutableList().apply {
                            removeAt(indexToRemove)
                        }
                        cartItemsState.value = cartItems

                        // Sauvegarde des modifications
                        CartUtil.saveCartItems(this@PanierActivity, cartItems)
                    }
                }
            }
        }
    }
}

object CartUtil {
    fun loadCartItems(context: Context): List<CartItem> {
        val gson = Gson()
        val cartItems = mutableListOf<CartItem>()
        val file = File(context.filesDir, "cart_items.json")

        if (file.exists()) {
            file.forEachLine { line ->
                cartItems.add(gson.fromJson(line, CartItem::class.java))
            }
        }

        return cartItems
    }

    fun countCartItemsLines(context: Context): Int {
        val file = File(context.filesDir, "cart_items.json")
        var linesCount = 0

        if (file.exists()) {
            file.forEachLine { _ ->
                linesCount++
            }
        }

        return linesCount
    }
    fun saveCartItems(context: Context, cartItems: List<CartItem>) {
        val gson = Gson()
        val file = File(context.filesDir, "cart_items.json")
        file.writeText("") // Effacer le contenu existant
        cartItems.forEach { item ->
            val cartItemJson = gson.toJson(item)
            file.appendText("$cartItemJson\n")
        }
    }
}

@Composable
fun PanierScreen(cartItems: List<CartItem>, innerPadding: PaddingValues, onItemRemoved: (Int) -> Unit) {
    LazyColumn(contentPadding = innerPadding) {
        itemsIndexed(cartItems) { index, item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Colonne pour le texte à gauche
                    Column(
                        modifier = Modifier
                            .weight(1f) // Prend tout l'espace disponible à gauche
                            .padding(end = 8.dp) // Un peu d'espace avant le bouton
                    ) {
                        Text(text = item.dishName)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Quantité: ${item.quantity}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Prix Total: ${item.totalPrice}€")
                    }
                    // Bouton à droite
                    Button(
                        onClick = { onItemRemoved(index) }
                    ) {
                        Text("Supprimer")
                    }
                }
            }
            Divider()
        }
    }
}




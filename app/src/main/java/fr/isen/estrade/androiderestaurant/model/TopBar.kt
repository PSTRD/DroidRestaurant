package fr.isen.estrade.androiderestaurant.model

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.estrade.androiderestaurant.PanierActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, cartItemCount: Int, showBackButton: Boolean, onNavigateBack: () -> Unit) {
    val context = LocalContext.current // Obtenez le contexte local dans le composant
    TopAppBar(
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Filled.ArrowBack, contentDescription = "Accueil",
                        modifier = Modifier.size(35.dp) // Taille personnalisée de l'icône
                    )
                }
            }
        },
        title = { Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        ) },
        actions = {
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = {
                    // Créer un intent pour démarrer PanierActivity
                    val intent = Intent(context, PanierActivity::class.java)
                    context.startActivity(intent) // Démarrer l'activité
                }) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Panier",
                        modifier = Modifier.size(35.dp) // Taille personnalisée de l'icône

                    )
                    if (cartItemCount > 0) {
                        // Positionnement de la pastille
                        Badge(count = cartItemCount)
                    }
                }
            }
        }
    )
}

@Composable
fun Badge(count: Int) {
    if (count > 0) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(18.dp)
                .background(Color.Red, CircleShape)

        ) {
            Text(text = "$count", color = Color.White, fontSize = 12.sp)
        }
    }
}


package fr.isen.estrade.androiderestaurant
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import fr.isen.estrade.androiderestaurant.model.Dish
import fr.isen.estrade.androiderestaurant.model.MenuResponse
import fr.isen.estrade.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.estrade.androiderestaurant.MenuDataRepository
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.compose.rememberImagePainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import fr.isen.estrade.androiderestaurant.ui.theme.AndroidERestaurantTheme

class DishDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dishName = intent.getStringExtra("DISH_NAME") ?: "Nom du plat non disponible"
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: ""
        val menuResponse: MenuResponse = MenuDataRepository.menuResponse ?: return // ou une valeur par défaut
        val dish = findDishInCategory(menuResponse, categoryName, dishName)
        Log.d("DishDetailActivity", "URLs des images: ${dish?.images?.joinToString(", ") ?: "Liste vide"}")

        setContent {
                AndroidERestaurantTheme {
                    DishDetailScreen(dish)
                }
            }
        }
}

@Composable
fun DishDetailScreen(dish: Dish?) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            Text(text = dish?.nameFr ?: "Nom du plat non disponible", style = MaterialTheme.typography.headlineMedium)
            // Afficher les images
            Column {
                dish?.images?.forEach { imageUrl ->
                    //Text(text = imageUrl) // Affiche chaque URL d'image comme texte
                    DishImage(url = imageUrl)
                }
            }
            // Afficher les prix
            dish?.prices?.forEach { price ->
                Text(text = "${price.size} : ${price.price}€")
            }
        }
    }
}

fun findDishInCategory(menuResponse: MenuResponse, categoryName: String, dishName: String): Dish? {
    // Trouver la catégorie spécifique par son nom
    val category = menuResponse.data.find { it.nameFr == categoryName }

    // Dans cette catégorie, chercher le plat correspondant par son nom
    val dish = category?.items?.find { it.nameFr == dishName }

    return dish
}


@Composable
fun DishImage(url: String) {
    val painter = rememberAsyncImagePainter(model = url)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth() // Utilise la largeur maximale disponible
            .aspectRatio(16f / 9f) // Définit un ratio d'aspect, par exemple 16:9
            .padding(4.dp), // Ajout d'un peu de padding autour de l'image
        contentScale = ContentScale.Crop // Cela permet de s'assurer que l'image remplit l'espace défini en conservant son ratio
    )
}
package fr.isen.estrade.androiderestaurant
import android.content.Intent
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
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import fr.isen.estrade.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import fr.isen.estrade.androiderestaurant.model.CartItem
import fr.isen.estrade.androiderestaurant.model.CartViewModel
import fr.isen.estrade.androiderestaurant.model.TopBar
import java.io.File

class DishDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dishName = intent.getStringExtra("DISH_NAME") ?: "Nom du plat non disponible"
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: ""
        val menuResponse: MenuResponse = MenuDataRepository.menuResponse ?: return // ou une valeur par défaut
        val dish = findDishInCategory(menuResponse, categoryName, dishName)
        Log.d("DishDetailActivity", "URLs des images: ${dish?.images?.joinToString(", ") ?: "Liste vide"}")
        val dishDetailViewModel by viewModels<CartViewModel>()
        val cartViewModel by viewModels<CartViewModel>()
        cartViewModel.updateItemCount(applicationContext)

        setContent {
            val cartItemCount = dishDetailViewModel.cartItemCount.value
            AndroidERestaurantTheme {
                Scaffold(
                    topBar = {
                        TopBar(title = "DroidRestaurant",
                            cartItemCount = cartItemCount,
                            showBackButton = true,
                            onNavigateBack = {
                                finish()
                                //val homeIntent = Intent(this, HomeActivity::class.java)
                                //homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                //this.startActivity(homeIntent)
                            }
                        )
                    }
                ) { innerPadding -> // Ici, vous devez utiliser le paramètre content de Scaffold
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding), // Utilisez innerPadding pour respecter les paddings de Scaffold
                        color = MaterialTheme.colorScheme.background
                    ) {
                        DishDetailScreen(dish)
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val cartViewModel by viewModels<CartViewModel>()
        cartViewModel.updateItemCount(applicationContext)
    }
}

@Composable
fun DishDetailScreen(dish: Dish?) {
    var quantity by remember { mutableStateOf(1) }
    val pricePerItem = dish?.prices?.firstOrNull()?.price?.toFloatOrNull() ?: 0f
    val totalPrice = pricePerItem * quantity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val cartViewModel: CartViewModel = viewModel()

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .align(Alignment.TopCenter) ){

                // Afficher les images si disponibles
                dish?.images?.let {
                    if (it.isNotEmpty()) {
                        ImageCarousel(images = it)
                    }
                }
                Text(
                    text = dish?.nameFr ?: "Nom du plat non disponible",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(10.dp)// Aligner à gauche dans le Column
                )

                // Affichage des ingrédients en une ligne, séparés par des virgules
                dish?.ingredients?.let { ingredients ->
                    if (ingredients.isNotEmpty()) {
                        val ingredientsText = ingredients.joinToString(separator = ", ") { it.nameFr }
                        Text(" $ingredientsText ",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(horizontal = 10.dp) // Ajoute du padding vertical
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // Ajoute un peu d'espace avant le sélecteur de quantité

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    QuantitySelector(
                        quantity = quantity,
                        onQuantityChange = { quantity = it })
                }
            }
            // Bouton d'achat avec prix total placé en bas
            Button(
                onClick = {
                    // Création de l'objet CartItem
                    val cartItem = CartItem(
                        dishId = dish?.id ?: "",
                        dishName = dish?.nameFr ?: "",
                        quantity = quantity,
                        unitPrice = totalPrice/quantity,
                        totalPrice = totalPrice
                    )

                    // Sérialisation en JSON
                    val gson = Gson()
                    val cartItemJson = gson.toJson(cartItem)

                    // Enregistrement dans un fichier (exemple simple)
                    val fileName = "cart_items.json"
                    Log.d("Cart", "Enregistrement de l'item au panier : $cartItemJson")
                    val file = File(context.filesDir, fileName)
                    file.appendText(cartItemJson + "\n") // Ajoute à un fichier existant ou crée un nouveau fichier
                    Log.d("Cart", "Item enregistré avec succès.")
                    if (file.exists()) {
                        val content = file.readText()
                        Log.d("Cart", "Contenu du fichier : $content")
                    } else {
                        Log.d("Cart", "Le fichier n'existe pas.")
                    }

                    cartViewModel.updateItemCount(context)

                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Article ajouté au panier",
                            actionLabel = "OK",
                            duration = SnackbarDuration.Short
                        )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                // Handle action performed
                            }
                            SnackbarResult.Dismissed -> {
                                // Handle dismissed
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(30.dp)
            ) {
                Text("TOTAL ${"%.2f".format(totalPrice)}€")
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
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
fun QuantitySelector(quantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
            Text("-")
        }
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = { onQuantityChange(quantity + 1) }) {
            Text("+")
        }
    }
}

@Composable
fun DishImage(url: String) {
    val painter = rememberAsyncImagePainter(model = url)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .padding(4.dp),
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(images: List<String>) {
    val count = images.size
    val pagerState = rememberPagerState(pageCount = {count})
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)

    ) { page ->
        Image(
            painter = rememberAsyncImagePainter(images[page]),
            contentDescription = "Image du plat",
            // Modifiez selon vos besoins, par exemple pour ajuster l'image
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp), // Fixer une hauteur peut aider à uniformiser l'affichage
            contentScale = ContentScale.Crop // Recadre l'image pour remplir l'espace tout en conservant les proportions

        )
    }
    //Indicators(pagerState.pageCount, pagerState.currentPage)

}


@Composable
fun DisplayImageUrls(imageUrls: List<String>) {
    Column {
        imageUrls.forEach { imageUrl ->
            Text(text = imageUrl)
        }
    }
}
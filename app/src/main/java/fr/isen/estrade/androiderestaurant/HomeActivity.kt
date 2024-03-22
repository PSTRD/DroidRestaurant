package fr.isen.estrade.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.estrade.androiderestaurant.model.CartViewModel
import fr.isen.estrade.androiderestaurant.model.TopBar
import fr.isen.estrade.androiderestaurant.ui.theme.AndroidERestaurantTheme


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                            showBackButton = false,
                            onNavigateBack = {
                                finish()
                                //val homeIntent = Intent(this, HomeActivity::class.java)
                                //homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
                        RestaurantMenu()
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeActivity", "L'activité Home est détruite.")
    }
    override fun onResume() {
        super.onResume()
        val cartViewModel by viewModels<CartViewModel>()
        cartViewModel.updateItemCount(applicationContext)
    }
}


@Composable
fun RestaurantMenu() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenue chez DroidRestaurant",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        MenuItem(name = "Entrées")
        Spacer(modifier = Modifier.height(8.dp))
        MenuItem(name = "Plats")
        Spacer(modifier = Modifier.height(8.dp))
        MenuItem(name = "Desserts")
    }
}

@Composable
fun MenuItem(name: String) {
    val context = LocalContext.current
    Text(
        text = name,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clickable {
                val intent = Intent(context, CategoryActivity::class.java).apply {
                    putExtra("categoryName", name)
                }
                context.startActivity(intent)
                //Toast.makeText(context, "Vous avez cliqué sur $name", Toast.LENGTH_SHORT).show()
            }
            .padding(vertical = 8.dp)
    )
    Divider(
        color = MaterialTheme.colorScheme.primary,
        thickness = 2.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun RestaurantMenuPreview() {
    AndroidERestaurantTheme {
        RestaurantMenu()
    }
}

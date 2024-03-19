package fr.isen.estrade.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.estrade.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import android.content.Intent
import android.util.Log
import android.widget.Toast


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RestaurantMenu()
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeActivity", "L'activité Home est détruite.")
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

package fr.isen.estrade.androiderestaurant

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.estrade.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.foundation.lazy.items
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import androidx.compose.runtime.State
import fr.isen.estrade.androiderestaurant.model.MenuResponse
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import org.json.JSONObject
import fr.isen.estrade.androiderestaurant.MenuDataRepository

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val menuResponseState = mutableStateOf<MenuResponse?>(null)
        val categoryName = intent.getStringExtra("categoryName") ?: ""

        fetchMenu(this,
            onResult = { menuResponse ->
                menuResponseState.value = menuResponse
            },
            onError = {
            }
        )

        setContent {
            AndroidERestaurantTheme {
                MenuScreen(menuResponse = menuResponseState,categoryName,context=this)
            }
        }
    }
}


fun fetchMenu(context: Context, onResult: (MenuResponse?) -> Unit, onError: (Exception) -> Unit) {
    val queue: RequestQueue = Volley.newRequestQueue(context)
    val url = "http://test.api.catering.bluecodegames.com/menu"

    val jsonRequestBody = JSONObject()
    jsonRequestBody.put("id_shop", "1")

    val stringRequest = object : StringRequest(Method.POST, url,
        Response.Listener { response ->
            try {
                val gson = Gson()
                val menuResponse = gson.fromJson(response, MenuResponse::class.java)
                MenuDataRepository.menuResponse = menuResponse
                onResult(menuResponse)
            } catch (e: Exception) {
                onError(e)
            }
        },
        Response.ErrorListener { error ->
            error.networkResponse?.let { response ->
                val responseBody = String(response.data)
                Log.e("API Error", "Status Code: ${response.statusCode} - Response: $responseBody")
            } ?: Log.e("API Error", "No network response received")
            onError(Exception(error.toString()))
        }
    ) {
        override fun getBodyContentType(): String = "application/json; charset=utf-8"

        override fun getBody(): ByteArray = jsonRequestBody.toString().toByteArray(Charsets.UTF_8)

    }

    queue.add(stringRequest)
}

@Composable
fun MenuScreen(menuResponse: State<MenuResponse?>, categoryName: String, context: Context) {
    // Récupération de la catégorie spécifique
    val category = menuResponse.value?.data?.find { it.nameFr == categoryName }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        category?.items?.let { items ->
            items(items = items) { dish ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {  // Ajout de l'interaction clickable ici
                            // Création de l'intent pour démarrer DishDetailActivity
                            val intent = Intent(context, DishDetailActivity::class.java).apply {
                                putExtra("DISH_NAME", dish.nameFr)  // Passage du nom du plat
                                putExtra("CATEGORY_NAME", categoryName)
                            }
                            context.startActivity(intent)  // Démarrage de l'activité
                        }
                ) {
                    Text(
                        text = dish.nameFr,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                    // Séparateur après chaque item sauf le dernier
                    if (dish != items.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}



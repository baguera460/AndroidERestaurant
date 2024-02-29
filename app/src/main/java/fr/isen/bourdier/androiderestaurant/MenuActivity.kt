package fr.isen.bourdier.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.volley.Cache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.GsonBuilder
import fr.isen.bourdier.androiderestaurant.basket.Basket
import fr.isen.bourdier.androiderestaurant.basket.BasketActivity
import fr.isen.bourdier.androiderestaurant.network.Category
import fr.isen.bourdier.androiderestaurant.network.Dish
import fr.isen.bourdier.androiderestaurant.network.MenuResult
import fr.isen.bourdier.androiderestaurant.network.NetworkConstants
import fr.isen.bourdier.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONObject


interface DishInterface {
    fun redirectToPage(activityClass: Class<*>)
}

class MenuActivity : ComponentActivity(), DishInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = intent.getStringExtra("type")
            ?.let { DishType.valueOf(it) }
            ?: DishType.STARTER

        setContent {
            AndroidERestaurantTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MenuView(this, type)
                }
            }
        }
    }

    override fun redirectToPage(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuView(activity: MenuActivity, type: DishType) {
    val category = remember { mutableStateOf<Category?>(null) }
    var refreshing by remember { mutableStateOf(false) }
    val queue by remember { mutableStateOf(Volley.newRequestQueue(activity)) }
    val currentCategory = type.title()

    Column {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    stringResource(id = R.string.app_name),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { activity.redirectToPage(HomeActivity::class.java) }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.amaze_logo),
                        contentDescription = "Amaze logo"
                    )
                }
            },
            actions = {
                IconButton(onClick = { activity.redirectToPage(BasketActivity::class.java) }) {
                    BadgedBox(
                        badge = {
                            Badge(
                                modifier = Modifier.offset(y=10.dp, x= (-5).dp),
                                containerColor = Color.LightGray
                            ){
                                val badgeNumber = Basket.current(LocalContext.current).items.size.toString()
                                Text(
                                    badgeNumber,
                                    modifier = Modifier.semantics {
                                        contentDescription = "$badgeNumber new notifications"
                                    }
                                )
                            }
                        }) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Basket"
                        )
                    }
                }
            }
        )
        Text(
            text = type.title(),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = {
            queue.cache.clear()
            postData(queue, category, currentCategory)
            refreshing = false
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            category.value?.let {
                items(it.items) { dish ->
                    DishRow(dish)
                }
            }
        }
    }

    RequestData(type, category, queue)
}

@Composable
fun DishRow(dish: Dish) {
    val context = LocalContext.current
    Card(border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(
                    "dish",
                    GsonBuilder()
                        .create()
                        .toJson(dish)
                )
                context.startActivity(intent)
            }
    ) {
        Row(Modifier.padding(8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(dish.images.first())
                    .build(),
                null,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(10))
                    .padding(8.dp)
            )
            Text(
                dish.name,
                Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(8.dp)
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${dish.prices.first().price} €",
                Modifier.align(alignment = Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun RequestData(type: DishType, category: MutableState<Category?>, queue: RequestQueue) {
    val currentCategory = type.title()
    val cache = queue.cache.get(NetworkConstants.URL)

    if (cache == null) {
        postData(queue, category, currentCategory)
    } else {
        val response = JSONObject(String(cache.data))
        category.value = getFilteredCategory(currentCategory, response)
    }
}

private fun postData(
    queue: RequestQueue,
    category: MutableState<Category?>,
    currentCategory: String
) {
    val params = JSONObject().let {
        it.put(NetworkConstants.ID_SHOP, "1")
        it
    }

    val request = JsonObjectRequest(
        Request.Method.POST,
        NetworkConstants.URL,
        params,
        { response ->
            category.value = getFilteredCategory(currentCategory, response)
            val cache = Cache.Entry().let {
                it.data = response.toString().toByteArray()
                it.ttl = 60 * 60 * 24 * 7
                it.serverDate = System.currentTimeMillis()
                it.responseHeaders = mutableMapOf("Cache-Control" to "max-age=0")
                it
            }
            queue.cache.put(NetworkConstants.URL, cache)
        },
        {
            Log.e("request", it.toString())
        }
    )

    queue.add(request)
}

private fun getFilteredCategory(currentCategory: String, response: JSONObject): Category {
    val result =
        GsonBuilder().create().fromJson(response.toString(), MenuResult::class.java)
    return result.data.first { it.name == currentCategory }
}
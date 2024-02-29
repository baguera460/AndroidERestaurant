package fr.isen.bourdier.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.gson.GsonBuilder
import fr.isen.bourdier.androiderestaurant.basket.Basket
import fr.isen.bourdier.androiderestaurant.basket.BasketActivity
import fr.isen.bourdier.androiderestaurant.network.Dish
import fr.isen.bourdier.androiderestaurant.ui.theme.AndroidERestaurantTheme
import kotlinx.coroutines.launch
import kotlin.math.max

interface DetailInterface {
    fun redirectToPage(activityClass : Class<*>)
}

class DetailActivity : ComponentActivity(), DetailInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dish = intent.getStringExtra("dish")
            ?.let { GsonBuilder().create().fromJson(it, Dish::class.java) }

        if (dish == null) {
            finish()
            return
        }

        setContent {
            AndroidERestaurantTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    DetailView(this, dish)
                }
            }
        }
    }

    override fun redirectToPage(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailView(activity: DetailInterface, dish: Dish) {
    val context = LocalContext.current
    val count = remember { mutableIntStateOf(1) }
    val ingredient = dish.ingredients.joinToString(", ") { it.name }
    val pagerState = rememberPagerState(pageCount = {
        dish.images.count()
    })
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = dish.name,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalPager(state = pagerState) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(dish.images[it])
                        .build(),
                    null,
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    error = painterResource(R.drawable.ic_launcher_foreground),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
            Text(ingredient)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                OutlinedButton(onClick = {
                    count.intValue = max(1, count.intValue - 1)
                }) {
                    Text("-")
                }
                Text(count.intValue.toString())
                OutlinedButton(onClick = {
                    count.intValue = count.intValue + 1
                }) {
                    Text("+")
                }
                Spacer(Modifier.weight(1f))
            }
            Button(onClick = {
                Basket.current(context).add(dish, count.intValue, context)
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        "Ajouté au panier",
                        "Annuler",
                        true,
                        SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        Basket.current(context)
                            .delete(Basket.current(context).items.last(), context)
                    }
                }
            }) {
                Text("Commander")
            }
            Button(onClick = {
                val intent = Intent(context, BasketActivity::class.java)
                context.startActivity(intent)
            }) {
                Text("Voir mon panier")
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}
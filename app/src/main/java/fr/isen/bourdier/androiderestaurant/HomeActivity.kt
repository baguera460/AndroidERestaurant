package fr.isen.bourdier.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.bourdier.androiderestaurant.basket.Basket
import fr.isen.bourdier.androiderestaurant.basket.BasketActivity
import fr.isen.bourdier.androiderestaurant.ui.theme.AndroidERestaurantTheme

enum class DishType {
    STARTER, MAIN, DESSERT;

    @Composable
    fun title(): String {
        return when (this) {
            STARTER -> stringResource(id = R.string.menu_starter)
            MAIN -> stringResource(id = R.string.menu_main)
            DESSERT -> stringResource(id = R.string.menu_dessert)
        }
    }
}

interface HomeInterface {
    fun onMenuClick(type: DishType, category: String)
    fun goToBasket()
}

class HomeActivity : ComponentActivity(), HomeInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeView(this)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("HomeActivity", "HomeActivity destroyed")
    }

    override fun onMenuClick(type: DishType, category: String) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("type", type.name)
        startActivity(intent)
    }

    override fun goToBasket() {
        val intent = Intent(this, BasketActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(activity: HomeActivity) {
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
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.amaze_logo),
                        contentDescription = "Amaze logo"
                    )
                }
            },
            actions = {
                IconButton(onClick = { activity.goToBasket() }) {
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
            text = "Bienvenue chez ${stringResource(id = R.string.app_name)}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    ) {
        ExtendedFloatingActionButton(
            onClick = { activity.onMenuClick(DishType.STARTER, "entrées") },
            icon = { },
            text = { Text(text = "Entrées") },
            modifier = Modifier.width(300.dp)
        )
        ExtendedFloatingActionButton(
            onClick = { activity.onMenuClick(DishType.MAIN, "plats") },
            icon = { },
            text = { Text(text = "Plats") },
            modifier = Modifier.width(300.dp)
        )
        ExtendedFloatingActionButton(
            onClick = { activity.onMenuClick(DishType.DESSERT, "desserts") },
            icon = { },
            text = { Text(text = "Desserts") },
            modifier = Modifier.width(300.dp)
        )
    }
}
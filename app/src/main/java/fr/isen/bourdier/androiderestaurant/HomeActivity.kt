package fr.isen.bourdier.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    override fun onMenuClick(type: DishType, category: String) {
        Toast.makeText(this, "Redirection vers les $category", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("type", type.name)
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
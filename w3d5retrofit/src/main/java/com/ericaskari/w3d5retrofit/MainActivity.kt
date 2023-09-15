package com.ericaskari.w3d5retrofit

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ericaskari.w3d5retrofit.data.AppViewModelProvider
import com.ericaskari.w3d5retrofit.entities.Actor
import com.ericaskari.w3d5retrofit.entities.ActorViewModel
import com.ericaskari.w3d5retrofit.entities.Movie
import com.ericaskari.w3d5retrofit.entities.MovieViewModel
import com.ericaskari.w3d5retrofit.forms.NewActorForm
import com.ericaskari.w3d5retrofit.forms.NewMovieForm
import com.ericaskari.w3d5retrofit.ui.theme.AppMaterialTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private val parentJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        coroutineScope.launch {
            delay(1000)
        }

        setContent {
            AppMaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "movies") {
                    composable("movies") { backStackEntry ->

                        Movies(navController)
                    }
                    composable(
                        "movies/{movieId}/actors",
                        arguments = listOf(navArgument("movieId") { type = NavType.StringType }),
                    ) { backStackEntry ->
                        Actors(backStackEntry.arguments?.getString("movieId"))
                    }
                }

            }
        }
    }
}

@Composable
fun Movies(navHostController: NavHostController, viewModel: MovieViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val items = viewModel.items.collectAsState(initial = mutableListOf())

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            val context = LocalContext.current

            NewMovieForm { movie, saved ->
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.save(movie)
                    saved(true)
                    Toast.makeText(context, "Movie Saved!", Toast.LENGTH_SHORT).show()
                }
            }
            MovieList(items.value) {
                navHostController.navigate("movies/${it}/actors")
            }
        }
    }

}


@Composable
fun Actors(movieId: String?, viewModel: ActorViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val items = viewModel.items.collectAsState(initial = mutableListOf())
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            val context = LocalContext.current

            NewActorForm(movieId = movieId) { movie, saved ->
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.save(movie)
                    saved(true)
                    Toast.makeText(context, "Actor Saved!", Toast.LENGTH_SHORT).show()
                }
            }
            ActorList(items.value.filter { it.movieId == movieId }) {
            }
        }
    }
}

@Composable
fun ActorList(data: List<Actor>, modifier: Modifier = Modifier, onClick: (id: String) -> Unit) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(data) {
            ActorListItem(data = it) { id ->
                onClick(id)
            }
        }
    }

}


@Composable
fun MovieList(data: List<Movie>, modifier: Modifier = Modifier, onClick: (id: String) -> Unit) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(data) {
            MovieListItem(data = it) { id ->
                onClick(id)
            }
        }
    }
}

@Composable
fun MovieListItem(data: Movie, onClick: (id: String) -> Unit) {
    ListItem(
        modifier = Modifier
            .clickable { onClick(data.id) },

        overlineContent = { Text("Movie") },
        headlineContent = { Text(data.name) },
        supportingContent = { Text("Director: ${data.director}") },
        leadingContent = {
            Icon(
                Icons.Filled.Info,
                contentDescription = "AccountCircle",
            )
        },
    )
}

@Composable
fun ActorListItem(data: Actor, onClick: (id: String) -> Unit) {
    ListItem(
        modifier = Modifier
            .clickable { onClick(data.id) },

        overlineContent = { Text("Actor") },
        headlineContent = { Text(data.name) },
        leadingContent = {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = "AccountCircle",
            )
        },
        trailingContent = { Text("nothing here") }
    )
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppMaterialTheme {
        Greeting("Android")
    }
}


//@Composable
//fun AppNavigationBar() {
//    var selectedItem by remember { mutableIntStateOf(0) }
//    val items = listOf("Songs", "Artists", "Playlists")
//
//
//
//    NavigationBar {
//        items.forEachIndexed { index, item ->
//            NavigationBarItem(
//                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
//                label = { Text(item) },
//                selected = selectedItem == index,
//                onClick = { selectedItem = index }
//            )
//        }
//    }
//}
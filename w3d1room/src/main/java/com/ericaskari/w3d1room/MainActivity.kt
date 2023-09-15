package com.ericaskari.w3d1room

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ericaskari.w3d1room.api.apimodels.President
import com.ericaskari.w3d1room.application.data.AppViewModelProvider
import com.ericaskari.w3d1room.metropolia.MetropoliaViewModel
import com.ericaskari.w3d1room.ui.theme.FirstComposeAppTheme
import com.ericaskari.w3d1room.wiki.WikiViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coroutineScope.launch {
            delay(1000)
        }

        setContent {
            FirstComposeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ApplicationContent()
                }
            }
        }
    }
}

@Composable
fun ApplicationContent(
    wikiViewModel: WikiViewModel = viewModel(factory = AppViewModelProvider.Factory),
    metropoliaViewModel: MetropoliaViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val items = metropoliaViewModel.presidents.collectAsState(initial = mutableListOf())

    PresidentList(items.value) {

    }

}

@Composable
fun PresidentListItem(item: President, onClick: (id: String) -> Unit) {
    ListItem(
        modifier = Modifier
            .clickable { onClick(item.name) },

        overlineContent = { Text("${item.startYear} - ${item.endYear}") },
        headlineContent = { Text(item.name) },
        leadingContent = {
            Icon(
                Icons.Filled.Info,
                contentDescription = "AccountCircle",
            )
        },
    )
}

@Composable
fun PresidentList(data: List<President>, modifier: Modifier = Modifier, onClick: (id: String) -> Unit) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(data) {
            PresidentListItem(item = it) { id ->
                onClick(id)
            }
        }
    }
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
    FirstComposeAppTheme {
        Greeting("Android")
    }
}
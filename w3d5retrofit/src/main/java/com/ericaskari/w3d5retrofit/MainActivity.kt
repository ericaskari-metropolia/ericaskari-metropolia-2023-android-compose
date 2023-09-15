package com.ericaskari.w3d5retrofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericaskari.w3d5retrofit.ui.theme.AppMaterialTheme
import com.ericaskari.w3d5retrofit.ui.theme.FirstComposeAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.await

class MainActivity : ComponentActivity() {
    private val service = RetrofitFactory.makeRetrofitService()

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
            AppMaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column {
                        val userList = remember {
                            mutableStateOf<List<UserModel>>(listOf())
                        }
                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val response = service.getUsers().await()
                                    CoroutineScope(Dispatchers.Main).launch {
                                        userList.value = response
                                    }
                                }
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Get Data")
                        }
                        UserList(userList.value)
                    }

                }
            }
        }
    }
}

@Composable
fun UserListItem(data: UserModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .padding(10.dp)
        .fillMaxWidth()
        .background(Color.Cyan),
        verticalArrangement = Arrangement.spacedBy(4.dp)

    ) {
        Text(text = data.name)
        Row {
            Text(text = data.username)
            Text(text = "-")
            Text(text = data.email)
        }
    }
}
@Composable
fun UserList(data: List<UserModel>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(data) {
            UserListItem(data = it)
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
    AppMaterialTheme {
        Greeting("Android")
    }
}
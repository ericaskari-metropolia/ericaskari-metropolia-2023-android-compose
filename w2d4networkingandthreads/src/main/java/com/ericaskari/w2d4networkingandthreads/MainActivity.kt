package com.ericaskari.w2d4networkingandthreads

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericaskari.w2d4networkingandthreads.ui.theme.FirstComposeAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    companion object {
        private val parentJob = Job()
        private val context = Dispatchers.Main + parentJob
        private val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)
        private val secondScope = CoroutineScope(Dispatchers.IO + parentJob)
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
                    Column {
                        val userList = remember {
                            mutableStateOf<List<UserModel>>(listOf())
                        }
                        Button(
                            onClick = {
                                sendRequest(
                                    userList = userList
                                )

                                Log.d("Main Activity", userList.toString())
                            },
                            modifier = Modifier.height(50.dp).fillMaxWidth()
                        ) {
                            Text(text = "Get Data")
                        }
                        UserList(userList.value)
                    }

                }
            }
        }
    }

    private fun sendRequest(
        userList: MutableState<List<UserModel>>
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(Api::class.java)

        val call: Call<List<UserModel>> = api.getUsers();

        call.enqueue(object: Callback<List<UserModel>> {
            override fun onResponse(call: Call<List<UserModel>>, response: Response<List<UserModel>>) {
                if(response.isSuccessful) {
                    Log.d("Main", "success!" + response.body().toString())
                    userList.value = response.body() ?: listOf()
                }
            }
            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                Log.e("Main", "Failed mate " + t.message.toString())
            }
        })
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
    FirstComposeAppTheme {
        Greeting("Android")
    }
}
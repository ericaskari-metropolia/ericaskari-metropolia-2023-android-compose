package com.ericaskari.w1d5composelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PresidentList(DataProvider.presidents)
        }
    }
}

@Composable
fun PresidentListItem(data: President, modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .padding(10.dp)
        .fillMaxWidth()
        .background(Color.Cyan),
        verticalArrangement = Arrangement.spacedBy(4.dp)

    ) {
        Text(text = data.name ?: "No Name")
        Row {
            Text(text = data.dutyStartYear.toString())
            Text(text = "-")
            Text(text = data.dutyEndYear.toString())
        }
    }
}
@Composable
fun PresidentList(data: List<President>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(data) {
            PresidentListItem(data = it)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PresidentListPreview() {
    PresidentList(data = DataProvider.presidents)
}
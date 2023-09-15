package com.ericaskari.w3d5retrofit.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ericaskari.w3d5retrofit.R
import com.ericaskari.w3d5retrofit.entities.Movie
import java.util.UUID.randomUUID

@Composable
fun NewMovieForm(onSave: (item: Movie, cleanup: (reset: Boolean) -> Unit) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var director by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = stringResource(id = R.string.add_movie),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = stringResource(id = R.string.movie_name),
            style = MaterialTheme.typography.bodyLarge
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            placeholder = { Text(text = "e.g. Hexamine") },
        )
        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = stringResource(id = R.string.director),
            style = MaterialTheme.typography.bodyLarge
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = director,
            onValueChange = { director = it },
            placeholder = { Text(text = "e.g. Alex") },
        )
        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = stringResource(id = R.string.year),
            style = MaterialTheme.typography.bodyLarge
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = year,
            onValueChange = { year = it },
            placeholder = { Text(text = "e.g. 1995") },
        )

        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                val item = Movie(
                    id = randomUUID().toString(),
                    name = name,
                    director = director,
                    year = year
                )
                onSave(item) {
                    if (it) {
                        name = ""
                        director = ""
                        year = ""

                    }
                }

            },
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text(
                text = stringResource(id = R.string.save),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

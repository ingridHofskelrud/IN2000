package no.uio.ifi.in2000.team33.smaafly.ui.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport

/**
 * Search bar for airports
 *
 * @param airports List of airports
 * @param onClick Function invoked when clicking on airport name
 * @param selectAirport Function invoked when selecting airport by searching
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    airports: List<Airport>,
    onClick: (String) -> Unit,
    selectAirport: (Airport) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    DockedSearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            onClick(it)
            query = ""
            active = false
        },
        active = active,
        onActiveChange = { active = !active },
        placeholder = { Text("Søk etter flyplass") },
        modifier = Modifier.padding(10.dp),
        trailingIcon = {
            IconButton(onClick = {
                query = ""
                active = false
            }) { Icon(Icons.Filled.Search, contentDescription = "Søk") }
        },
        leadingIcon = {
            if (!active) IconButton(onClick = { active = true }) {
                Icon(
                    Icons.Filled.KeyboardArrowDown, contentDescription = null
                )
            }
            else IconButton(onClick = { active = false }) {
                Icon(
                    Icons.Filled.KeyboardArrowUp, contentDescription = null
                )
            }
        }) {
        LazyColumn {
            items(airports.filter {
                it.name.contains(
                    query.trim(),
                    ignoreCase = true
                ) || it.icao!!.contains(query.trim(), ignoreCase = true)
            }.take(10)) {
                Text(
                    text = it.name,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .clickable(
                            onClickLabel = "Velg flyplass"
                        ) {
                            selectAirport(it)
                            active = false
                            query = ""
                        }
                        .padding(5.dp)
                )
            }
        }
    }
}

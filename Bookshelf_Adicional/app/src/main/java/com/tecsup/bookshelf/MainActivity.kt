package com.tecsup.bookshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tecsup.bookshelf.data.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val service = Retrofit.Builder().baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build().create(BooksApiService::class.java)
        val repository = NetworkBooksRepository(service)
        setContent { MaterialTheme {
            val model: BooksViewModel = viewModel(factory = object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T: ViewModel> create(modelClass: Class<T>): T = BooksViewModel(repository) as T
            })
            BooksScreen(model)
        } }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(model: BooksViewModel) {
    var query by remember { mutableStateOf("jazz history") }
    LaunchedEffect(Unit) { model.search(query) }
    Scaffold(topBar = { TopAppBar(title = { Text("Bookshelf") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Henrry Manuel Cuti Sencia", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(query, { query = it }, label = { Text("Buscar libros") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Button(onClick = { model.search(query) }, enabled = query.isNotBlank()) { Text("Buscar") }
            when (val state = model.state) {
                BooksUiState.Loading -> { CircularProgressIndicator(); Text("Estoy cargando libros y portadas…") }
                BooksUiState.Error -> { Text("No pude cargar los libros. Revisa tu conexión e inténtalo otra vez."); Button(onClick = { model.search(query) }) { Text("Reintentar") } }
                is BooksUiState.Success -> {
                    if (state.books.isEmpty()) Text("No encontré libros para esta búsqueda.")
                    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.books, key = { it.id }) { book ->
                            Card(Modifier.fillMaxWidth()) {
                                if (book.coverUrl() != null) AsyncImage(model = book.coverUrl(), contentDescription = "Portada de ${book.volumeInfo.title}", modifier = Modifier.fillMaxWidth().height(190.dp), contentScale = ContentScale.Fit)
                                else Text("Sin portada", Modifier.height(190.dp).padding(20.dp))
                                Text(book.volumeInfo.title, Modifier.padding(12.dp), style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

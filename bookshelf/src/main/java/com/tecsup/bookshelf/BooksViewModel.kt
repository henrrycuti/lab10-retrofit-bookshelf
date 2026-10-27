package com.tecsup.bookshelf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.bookshelf.data.Book
import com.tecsup.bookshelf.data.BooksRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface BooksUiState {
    data object Loading: BooksUiState
    data class Success(val books: List<Book>): BooksUiState
    data object Error: BooksUiState
}
class BooksViewModel(private val repository: BooksRepository): ViewModel() {
    var state: BooksUiState by mutableStateOf(BooksUiState.Loading)
        private set
    private var request: Job? = null
    fun search(query: String) {
        if (query.isBlank()) return
        request?.cancel()
        request = viewModelScope.launch {
            state = BooksUiState.Loading
            state = try { BooksUiState.Success(repository.search(query.trim())) }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { BooksUiState.Error }
        }
    }
}

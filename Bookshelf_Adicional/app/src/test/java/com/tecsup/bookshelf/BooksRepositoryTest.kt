package com.tecsup.bookshelf

import com.tecsup.bookshelf.data.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class BooksRepositoryTest {
    private class FakeApi(private val fail: Boolean = false, private val empty: Boolean = false): BooksApiService {
        val requested = mutableListOf<String>()
        override suspend fun search(query: String, limit: Int): BooksResponse {
            if (fail) throw IOException("Sin conexión")
            return BooksResponse(if (empty) emptyList() else listOf(Book("abc", VolumeInfo("Resumen"))))
        }
        override suspend fun detail(id: String): Book {
            requested.add(id)
            return Book(id, VolumeInfo("Libro completo", ImageLinks("http://books.google.com/cover")))
        }
    }
    @Test fun loadsDetailsAndUsesHttps() = runBlocking {
        val api = FakeApi()
        val books = NetworkBooksRepository(api).search("kotlin")
        assertEquals(listOf("abc"), api.requested)
        assertEquals("Libro completo", books.single().volumeInfo.title)
        assertEquals("https://books.google.com/cover", books.single().coverUrl())
    }
    @Test fun emptyResultsDoNotRequestDetails() = runBlocking {
        val api = FakeApi(empty = true)
        assertTrue(NetworkBooksRepository(api).search("nada").isEmpty())
        assertTrue(api.requested.isEmpty())
    }
    @Test(expected = IOException::class) fun networkErrorsReachCaller(): Unit = runBlocking {
        NetworkBooksRepository(FakeApi(fail = true)).search("kotlin")
        Unit
    }
    @Test fun missingCoverIsSupported() { assertNull(Book("x", VolumeInfo()).coverUrl()) }
}

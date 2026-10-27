package com.tecsup.bookshelf.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class BooksResponse(val items: List<Book> = emptyList())
data class Book(val id: String, val volumeInfo: VolumeInfo)
data class VolumeInfo(val title: String = "Sin título", val imageLinks: ImageLinks? = null)
data class ImageLinks(val thumbnail: String? = null)
interface BooksApiService {
    @GET("volumes") suspend fun search(@Query("q") query: String, @Query("maxResults") limit: Int = 12): BooksResponse
    @GET("volumes/{id}") suspend fun detail(@Path("id") id: String): Book
}
interface BooksRepository { suspend fun search(query: String): List<Book> }
class NetworkBooksRepository(private val service: BooksApiService): BooksRepository {
    override suspend fun search(query: String): List<Book> =
        service.search(query).items.map { service.detail(it.id) }
}
fun Book.coverUrl(): String? = volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")

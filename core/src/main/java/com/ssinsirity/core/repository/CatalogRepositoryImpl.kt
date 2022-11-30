package com.ssinsirity.core.repository

import com.ssinsirity.core.constants.Collections
import com.ssinsirity.core.data.entity.*
import com.ssinsirity.core.data.model.Catalog
import com.ssinsirity.core.data.toModel
import com.ssinsirity.core.data.toResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class CatalogRepositoryImpl(
    ioScope: CoroutineContext
) : FirebaseRepository(ioScope), CatalogRepository {

    @Inject
    constructor() : this(Dispatchers.IO)

    override suspend fun fetchCatalogs(onResult: (Result<List<Catalog>>) -> Unit) {
        val listOfCatalogsResponse =
            fs.collection(Collections.CATALOG).getAwaitObjects<CatalogResponse>()

        val listOfCatalogsModel = listOfCatalogsResponse.map { response ->
            val publisherResponse = docRefOf(Collections.PUBLISHER, response.publisherId)
                .getAwaitObject<PublisherResponse>()!!
            val bookResponse = docRefOf(Collections.BOOK, response.bookId)
                .getAwaitObject<BookResponse>()!!
            val genreResponse = docRefOf(Collections.GENRE, bookResponse.genreId)
                .getAwaitObject<GenreResponse>()!!
            val authorResponse = docRefOf(Collections.AUTHOR, bookResponse.authorId)
                .getAwaitObject<AuthorResponse>()!!

            val author = authorResponse.toModel()
            val genre = genreResponse.toModel()
            val publisher = publisherResponse.toModel()
            val book = bookResponse.toModel(author, genre)

            return@map response.toModel(book, publisher)
        }
        onResult(Result.success(listOfCatalogsModel))
    }

    override suspend fun postCatalog(catalog: Catalog, onResult: (Result<Catalog>) -> Unit) {
        withContext(ioScope) {
            val catalogResponse = catalog.toResponse()
            val publisherResponse = catalog.publisher.toResponse()
            val bookResponse = catalog.book.toResponse()
            val authorResponse = catalog.book.author.toResponse()
            val genreResponse = catalog.book.genre.toResponse()

            val catalogDocRef = docRefOf(Collections.CATALOG, catalogResponse.id)
            val publisherDocRef = docRefOf(Collections.PUBLISHER, publisherResponse.id)
            val bookDocRef = docRefOf(Collections.BOOK, bookResponse.id)
            val authorDocRef = docRefOf(Collections.AUTHOR, authorResponse.id)
            val genreDocRef = docRefOf(Collections.GENRE, genreResponse.id)

            fs.runBatch {
                with(it) {
                    set(catalogDocRef, catalogResponse)
                    set(publisherDocRef, publisherResponse)
                    set(bookDocRef, bookResponse)
                    set(authorDocRef, authorResponse)
                    set(genreDocRef, genreResponse)
                }
            }
                .addOnSuccessListener { onResult(Result.success(catalog)) }
                .addOnFailureListener { onResult(Result.failure(it)) }
        }
    }


    override suspend fun updateCatalog(
        catalog: Catalog,
        amount: Int,
        onResult: (Result<Catalog>) -> Unit
    ) {
        withContext(ioScope) {
            val updatedCatalog = catalog.copy(amount = amount).toResponse()

            docRefOf(Collections.CATALOG, catalog.id)
                .set(updatedCatalog)
        }
    }
}
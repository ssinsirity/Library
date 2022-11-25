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
        // val catalogs = mutableListOf<Catalog>()

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

/*
        fs.collection(Collections.CATALOG).get()
            .addOnFailureListener { onResult(Result.failure(it)) }
            .addOnSuccessListener { snapshot ->
                val listOfCatalogsResponse = snapshot.toObjects<CatalogResponse>()
                Log.d("filteredCatalogs", "response $listOfCatalogsResponse")
                listOfCatalogsResponse.forEach { response ->
                    fs.runTransaction {
                        with(it) {
                            val publisherResponse = get(
                                docRefOf(Collections.PUBLISHER, response.publisherId)
                            ).toObject<PublisherResponse>()!!

                            val bookResponse = get(
                                docRefOf(Collections.BOOK, response.bookId)
                            ).toObject<BookResponse>()!!

                            val genreResponse = get(
                                docRefOf(Collections.GENRE, bookResponse.genreId)
                            ).toObject<GenreResponse>()!!

                            val authorResponse = get(
                                docRefOf(Collections.AUTHOR, bookResponse.authorId)
                            ).toObject<AuthorResponse>()!!

                            val authorModel = authorResponse.toModel()
                            val genreModel = genreResponse.toModel()
                            val publisherModel = publisherResponse.toModel()

                            val bookModel = bookResponse.toModel(authorModel, genreModel)
                            val catalogModel = response.toModel(bookModel, publisherModel)

                            catalogs.add(catalogModel)
                        }
                    }
                }.also {
                    onResult(Result.success(catalogs))
                }
            }
*/
    }

    /* // todo temp realisation
     override suspend fun fetchCatalogs(genre: Genre): Flow<List<Catalog>> =
         fetchCatalogs().onEach { it.filter { catalog -> catalog.book.genre == genre } }

     // todo temp realisation
     override suspend fun fetchCatalogs(author: Author): Flow<List<Catalog>> =
         fetchCatalogs().onEach { it.filter { catalog -> catalog.book.author == author } }

     // todo temp realisation
     override suspend fun fetchCatalogs(title: String): Flow<List<Catalog>> =
         fetchCatalogs().onEach { it.filter { catalog -> catalog.book.title.startsWith(title) } }
 */

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

    override suspend fun postCatalogs(
        catalogs: Collection<Catalog>,
        onResult: (Result<Collection<Catalog>>) -> Unit
    ) {
        catalogs.forEach { catalog ->
            postCatalog(catalog) { result ->
                result.onFailure { onResult(Result.failure(it)) }
            }
        }
        onResult(Result.success(catalogs))
    }
}
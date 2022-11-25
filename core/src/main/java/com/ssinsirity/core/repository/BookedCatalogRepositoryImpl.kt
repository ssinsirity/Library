package com.ssinsirity.core.repository

import com.google.firebase.firestore.ktx.toObject
import com.ssinsirity.core.constants.Collections
import com.ssinsirity.core.data.entity.*
import com.ssinsirity.core.data.model.BookedCatalog
import com.ssinsirity.core.data.model.Catalog
import com.ssinsirity.core.data.toModel
import com.ssinsirity.core.data.toResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class BookedCatalogRepositoryImpl @Inject constructor(
    private val userSharedPref: UserSharedPref,
    ioScope: CoroutineContext = Dispatchers.IO
) : FirebaseRepository(ioScope), BookedCatalogRepository {

    override suspend fun fetchBookedCatalogs(
        userId: String?,
        onResult: (Result<List<BookedCatalog>>) -> Unit
    ) = withContext(ioScope) {
        val listOfBookedCatalogsResponse =
            fs.collection(Collections.BOOKED_CATALOG).getAwaitObjects<BookedCatalogResponse>()

        val listOfBookedCatalogs = listOfBookedCatalogsResponse.map { response ->
            val readerCardResponse = docRefOf(Collections.READER, response.readerId)
                .getAwaitObject<ReaderCardResponse>()!!
            val librarianResponse = docRefOf(Collections.LIBRARIAN, response.librarianId)
                .getAwaitObject<LibrarianResponse>()!!
            val catalogResponse = docRefOf(Collections.CATALOG, response.catalogId)
                .getAwaitObject<CatalogResponse>()!!
            val publisherResponse = docRefOf(Collections.PUBLISHER, catalogResponse.publisherId)
                .getAwaitObject<PublisherResponse>()!!
            val bookResponse = docRefOf(Collections.BOOK, catalogResponse.bookId)
                .getAwaitObject<BookResponse>()!!
            val authorResponse = docRefOf(Collections.AUTHOR, bookResponse.authorId)
                .getAwaitObject<AuthorResponse>()!!
            val genreResponse = docRefOf(Collections.GENRE, bookResponse.genreId)
                .getAwaitObject<GenreResponse>()!!

            val genre = genreResponse.toModel()
            val author = authorResponse.toModel()
            val book = bookResponse.toModel(author, genre)
            val publisher = publisherResponse.toModel()
            val catalog = catalogResponse.toModel(book, publisher)
            val reader = readerCardResponse.toModel()
            val librarian = librarianResponse.toModel()

            return@map response.toModel(catalog, reader, librarian)
        }
        val filteredBookedCatalogs = if (userId == null)
            listOfBookedCatalogs
        else
            listOfBookedCatalogs.filter { order -> order.reader.id == userId }

        onResult(Result.success(filteredBookedCatalogs))
    }

    override suspend fun bookCatalog(
        catalog: Catalog,
        onResult: (Result<Catalog>) -> Unit
    ) {
        if (catalog.amount <= 0) {
            onResult(Result.failure(Exception("Not enough amount of catalogs.")))
        }

        val adjustedAmountCatalog = catalog.copy(amount = catalog.amount - 1)

        val reader = userSharedPref.currentReader

        val librarian = withContext(ioScope) {
            fs.collection(Collections.LIBRARIAN)
                .get().await()
                .documents.random()
                .toObject<LibrarianResponse>()!!
                .toModel()
        }

        val bookedCatalogResponse = BookedCatalog(
            id = UUID.randomUUID().toString(),
            catalog = adjustedAmountCatalog,
            reader = reader,
            librarian = librarian,
            bookedDate = Instant.now()
        ).toResponse()

        withContext(ioScope) {
            docRefOf(Collections.BOOKED_CATALOG, bookedCatalogResponse.id)
                .set(bookedCatalogResponse)
                .addOnSuccessListener { onResult(Result.success(catalog)) }
                .addOnFailureListener { onResult(Result.failure(it)) }
        }
    }
}
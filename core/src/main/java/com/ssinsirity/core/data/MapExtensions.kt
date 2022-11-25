package com.ssinsirity.core.data

import com.google.firebase.Timestamp
import com.ssinsirity.core.data.entity.*
import com.ssinsirity.core.data.model.*
import java.util.*

fun AuthorResponse.toModel() = Author(
    id = id,
    firstName = firstName,
    lastName = lastName
)

fun Author.toResponse() = AuthorResponse(
    id = id,
    firstName = firstName,
    lastName = lastName
)


fun BookedCatalogResponse.toModel(
    catalog: Catalog,
    reader: ReaderCard,
    librarian: Librarian
) = BookedCatalog(
    id = id,
    catalog = catalog,
    reader = reader,
    librarian = librarian,
    bookedDate = bookedDate.toDate().toInstant(),
    returnDate = returnDate.toDate().toInstant(),
    actualReturnDate = actualReturnDate.toDate().toInstant()
)

fun BookedCatalog.toResponse() = BookedCatalogResponse(
    id = id,
    catalogId = catalog.id,
    readerId = reader.id,
    librarianId = librarian.id,
    bookedDate = Timestamp(Date.from(bookedDate)),
    returnDate = Timestamp(Date.from(returnDate)),
    actualReturnDate = Timestamp(Date.from(actualReturnDate))
)


fun BookResponse.toModel(author: Author, genre: Genre) = Book(
    id = id,
    title = title,
    annotation = annotation,
    author = author,
    genre = genre,
    published = publishedDate.toDate().toInstant()
)

fun Book.toResponse() = BookResponse(
    id = id,
    title = title,
    annotation = annotation,
    publishedDate = Timestamp(Date.from(published)),
    authorId = author.id,
    genreId = genre.id
)


fun CatalogResponse.toModel(book: Book, publisher: Publisher) = Catalog(
    id = id,
    book = book,
    publisher = publisher,
    amount = amount
)

fun Catalog.toResponse() = CatalogResponse(
    id = id,
    bookId = book.id,
    publisherId = publisher.id,
    amount = amount
)


fun GenreResponse.toModel() = Genre.findValue(name)
fun Genre.toResponse() = GenreResponse(
    id = id,
    name = name
)


fun LibrarianResponse.toModel() = Librarian(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName
)

fun Librarian.toResponse() = LibrarianResponse(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName
)


fun PublisherResponse.toModel() = Publisher(
    id = id,
    name = name
)

fun Publisher.toResponse() = PublisherResponse(
    id = id,
    name = name
)


fun ReaderCardResponse.toModel() = ReaderCard(
    id = id,
    email = email,
    cardNumber = cardNumber,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber
)

fun ReaderCard.toResponse() = ReaderCardResponse(
    id = id,
    email = email,
    cardNumber = cardNumber,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber
)

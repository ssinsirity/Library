package com.ssinsirity.core.repository

import com.google.firebase.firestore.ktx.toObjects
import com.ssinsirity.core.constants.Collections
import com.ssinsirity.core.data.entity.ReaderCardResponse
import com.ssinsirity.core.data.model.Librarian
import com.ssinsirity.core.data.model.ReaderCard
import com.ssinsirity.core.data.toModel
import com.ssinsirity.core.data.toResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class UserRepositoryImpl(
    ioScope: CoroutineContext
) : FirebaseRepository(ioScope), UserRepository {

    @Inject
    constructor() : this(Dispatchers.IO)

    override suspend fun post(
        reader: ReaderCard,
        onResult: (Result<ReaderCard>) -> Unit
    ) {
        withContext(ioScope) {
            val readerResponse = reader.toResponse()

            docRefOf(Collections.READER, readerResponse.id)
                .set(readerResponse)
                .addOnSuccessListener { onResult(Result.success(reader)) }
                .addOnFailureListener { onResult(Result.failure(it)) }
        }
    }

    override suspend fun post(
        librarian: Librarian,
        onResult: (Result<Librarian>) -> Unit
    ) {
        withContext(ioScope) {
            val librarianResponse = librarian.toResponse()

            docRefOf(Collections.LIBRARIAN, librarianResponse.id)
                .set(librarianResponse)
                .addOnSuccessListener { onResult(Result.success(librarian)) }
                .addOnFailureListener { onResult(Result.failure(it)) }
        }
    }

    override suspend fun fetchReaders(onResult: (Result<List<ReaderCard>>) -> Unit) {
        withContext(ioScope) {
            fs.collection(Collections.READER).get()
                .addOnSuccessListener { query ->
                    val readers = query.toObjects<ReaderCardResponse>().map { it.toModel() }
                    onResult(Result.success(readers))
                }.addOnFailureListener { onResult(Result.failure(it)) }
        }
    }
}
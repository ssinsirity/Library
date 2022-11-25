package com.ssinsirity.core.repository

import com.ssinsirity.core.data.model.Librarian
import com.ssinsirity.core.data.model.ReaderCard

interface UserRepository {
    suspend fun post(reader: ReaderCard, onResult: (Result<ReaderCard>) -> Unit)
    suspend fun post(librarian: Librarian, onResult: (Result<Librarian>) -> Unit)
    suspend fun fetchReaders(onResult: (Result<List<ReaderCard>>) -> Unit)
}
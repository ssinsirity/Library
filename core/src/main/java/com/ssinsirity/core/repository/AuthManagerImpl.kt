package com.ssinsirity.core.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ssinsirity.core.data.UserCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AuthManagerImpl(
    ioScope: CoroutineContext
) : FirebaseRepository(ioScope), AuthManager {

    @Inject
    constructor() : this(Dispatchers.IO)

    private val firebaseAuth = Firebase.auth

    override suspend fun register(
        credentials: UserCredentials,
        onResult: (Result<UserCredentials>) -> Unit
    ) {
        withContext(ioScope) {
            firebaseAuth.createUserWithEmailAndPassword(credentials.email, credentials.password)
                .addOnSuccessListener { onResult(Result.success(credentials)) }
                .addOnFailureListener { onResult(Result.failure(it)) }
        }
    }

    override suspend fun login(
        credentials: UserCredentials,
        onResult: (Result<UserCredentials>) -> Unit
    ) {
        withContext(ioScope) {
            firebaseAuth.signInWithEmailAndPassword(credentials.email, credentials.password)
                .addOnSuccessListener { onResult(Result.success(credentials)) }
                .addOnFailureListener { onResult(Result.failure(it)) }
        }
    }

    override suspend fun logOut() {
        withContext(ioScope) {
            firebaseAuth.signOut()
        }
    }
}
package com.ssinsirity.core.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

abstract class FirebaseRepository(
    protected val ioScope: CoroutineContext
) {
    protected val fs = Firebase.firestore

    protected fun docRefOf(collectionPath: String, documentPath: String) =
        fs.collection(collectionPath).document(documentPath)

    protected suspend inline fun <reified T : Any> CollectionReference.getAwaitObjects(): List<T> =
        get().await().toObjects()

    protected suspend inline fun <reified T> DocumentReference.getAwaitObject(): T? =
        get().await().toObject<T>()
}
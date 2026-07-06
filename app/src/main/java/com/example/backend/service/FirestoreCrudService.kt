package com.example.backend.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Generic Reusable CRUD Service for Cloud Firestore.
 * Utilizes suspending coroutines with Tasks.await() for clean, sequential asynchronous code.
 */
class FirestoreCrudService(private val db: FirebaseFirestore = FirebaseService.firestore) {

    private val TAG = "FirestoreCrudService"

    /**
     * Create or Overwrite a document in a specified collection.
     */
    suspend fun <T : Any> create(collection: String, docId: String, data: T): ResourceState<String> {
        return try {
            Log.d(TAG, "Creating doc in $collection with ID $docId")
            db.collection(collection).document(docId).set(data).await()
            ResourceState.Success(docId, "Successfully saved document '$docId' in collection '$collection'.")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating doc in $collection", e)
            ResourceState.Error(e, "Failed to write data to '$collection': ${e.localizedMessage ?: "Unknown Error"}")
        }
    }

    /**
     * Read a specific document by ID.
     */
    suspend fun <T : Any> read(collection: String, docId: String, clazz: Class<T>): ResourceState<T> {
        return try {
            Log.d(TAG, "Reading doc from $collection with ID $docId")
            val snapshot = db.collection(collection).document(docId).get().await()
            val obj = snapshot.toObject(clazz)
            if (obj != null) {
                ResourceState.Success(obj, "Successfully loaded document '$docId' from '$collection'.")
            } else {
                ResourceState.Error(
                    NoSuchElementException("Document $docId does not exist."), 
                    "Document with ID '$docId' does not exist in collection '$collection'."
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading doc from $collection", e)
            ResourceState.Error(e, "Failed to read document from '$collection': ${e.localizedMessage ?: "Unknown Error"}")
        }
    }

    /**
     * Update specific fields of a document using a Map.
     */
    suspend fun update(collection: String, docId: String, updates: Map<String, Any>): ResourceState<String> {
        return try {
            Log.d(TAG, "Updating doc in $collection with ID $docId")
            db.collection(collection).document(docId).update(updates).await()
            ResourceState.Success(docId, "Successfully updated document '$docId' in '$collection'.")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating doc in $collection", e)
            ResourceState.Error(e, "Failed to update document '$docId' in '$collection': ${e.localizedMessage ?: "Unknown Error"}")
        }
    }

    /**
     * Delete a document.
     */
    suspend fun delete(collection: String, docId: String): ResourceState<String> {
        return try {
            Log.d(TAG, "Deleting doc from $collection with ID $docId")
            db.collection(collection).document(docId).delete().await()
            ResourceState.Success(docId, "Successfully deleted document '$docId' from '$collection'.")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting doc from $collection", e)
            ResourceState.Error(e, "Failed to delete document '$docId' from '$collection': ${e.localizedMessage ?: "Unknown Error"}")
        }
    }

    /**
     * Fetch all documents from a collection.
     */
    suspend fun <T : Any> getAll(collection: String, clazz: Class<T>): ResourceState<List<T>> {
        return try {
            Log.d(TAG, "Getting all docs from $collection")
            val snapshot = db.collection(collection).get().await()
            val list = snapshot.toObjects(clazz)
            ResourceState.Success(list, "Successfully retrieved ${list.size} records from '$collection'.")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all docs from $collection", e)
            ResourceState.Error(e, "Failed to load records from '$collection': ${e.localizedMessage ?: "Unknown Error"}")
        }
    }

    /**
     * Fetch filtered documents matching an equality condition.
     */
    suspend fun <T : Any> getFiltered(collection: String, field: String, value: Any, clazz: Class<T>): ResourceState<List<T>> {
        return try {
            Log.d(TAG, "Querying $collection where $field == $value")
            val snapshot = db.collection(collection).whereEqualTo(field, value).get().await()
            val list = snapshot.toObjects(clazz)
            ResourceState.Success(list, "Successfully retrieved ${list.size} filtered records from '$collection'.")
        } catch (e: Exception) {
            Log.e(TAG, "Error querying $collection", e)
            ResourceState.Error(e, "Failed to execute query on '$collection': ${e.localizedMessage ?: "Unknown Error"}")
        }
    }
}

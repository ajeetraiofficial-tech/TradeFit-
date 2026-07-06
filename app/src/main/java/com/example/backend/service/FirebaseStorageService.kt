package com.example.backend.service

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * Service to handle document and media file uploads/downloads with Firebase Storage.
 */
class FirebaseStorageService(
    private val storage: FirebaseStorage = FirebaseService.storage
) {
    private val TAG = "FirebaseStorageService"

    /**
     * Upload a local Uri to a specific Storage Path.
     */
    suspend fun uploadFile(path: String, fileUri: Uri): ResourceState<String> {
        return try {
            Log.d(TAG, "Uploading file Uri to: $path")
            val ref = storage.reference.child(path)
            ref.putFile(fileUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            ResourceState.Success(downloadUrl, "File uploaded successfully to storage.")
        } catch (e: Exception) {
            Log.e(TAG, "File upload failed", e)
            ResourceState.Error(e, "Failed to upload file: ${e.localizedMessage ?: "Unknown network error"}")
        }
    }

    /**
     * Upload a raw byte array (useful for dynamically generated PDF documents/reports) to a specific Storage Path.
     */
    suspend fun uploadBytes(path: String, bytes: ByteArray, contentType: String = "application/pdf"): ResourceState<String> {
        return try {
            Log.d(TAG, "Uploading ${bytes.size} bytes to: $path")
            val ref = storage.reference.child(path)
            
            val metadata = com.google.firebase.storage.StorageMetadata.Builder()
                .setContentType(contentType)
                .build()
                
            ref.putBytes(bytes, metadata).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            ResourceState.Success(downloadUrl, "Document generated and saved to Firebase Storage.")
        } catch (e: Exception) {
            Log.e(TAG, "Bytes upload failed", e)
            ResourceState.Error(e, "Failed to write generated document to storage: ${e.localizedMessage}")
        }
    }

    /**
     * Delete a file from Storage.
     */
    suspend fun deleteFile(path: String): ResourceState<Unit> {
        return try {
            Log.d(TAG, "Deleting file at path: $path")
            val ref = storage.reference.child(path)
            ref.delete().await()
            ResourceState.Success(Unit, "File deleted from storage.")
        } catch (e: Exception) {
            Log.e(TAG, "File deletion failed", e)
            ResourceState.Error(e, "Failed to delete file from storage: ${e.localizedMessage}")
        }
    }

    /**
     * Fetch download URL for a file in Storage.
     */
    suspend fun getDownloadUrl(path: String): ResourceState<String> {
        return try {
            Log.d(TAG, "Fetching download URL for: $path")
            val ref = storage.reference.child(path)
            val url = ref.downloadUrl.await().toString()
            ResourceState.Success(url, "Download URL fetched successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch download URL", e)
            ResourceState.Error(e, "Failed to obtain file link: ${e.localizedMessage}")
        }
    }
}

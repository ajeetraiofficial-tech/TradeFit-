package com.example.backend.service

/**
 * Reusable ResourceState wrapper for modern Jetpack Compose State Management.
 * Accurately models Loading, Success (with optional message), and Error (with exception) states.
 */
sealed class ResourceState<out T> {
    object Idle : ResourceState<Nothing>()
    object Loading : ResourceState<Nothing>()
    
    data class Success<out T>(
        val data: T, 
        val message: String? = null
    ) : ResourceState<T>()
    
    data class Error(
        val exception: Throwable, 
        val message: String
    ) : ResourceState<Nothing>()
}

package com.example.backend.service

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.example.BuildConfig

/**
 * Centralized Firebase Service for TradeFit ERP.
 * Configures Firebase Authentication, Cloud Firestore, and Firebase Storage.
 * Fully loads config from environment variables via Secrets Gradle Plugin / BuildConfig.
 */
object FirebaseService {
    private const val TAG = "FirebaseService"
    
    // Lazily initialized service instances
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    /**
     * Initializes Firebase programmatically if not already done.
     * Uses environment configuration values loaded from .env file via BuildConfig.
     * If no env variables are specified, it gracefully attempts fallback to standard JSON initialization.
     */
    fun initialize(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val apiKey = try { BuildConfig.FIREBASE_API_KEY } catch (e: Exception) { "" }
                val projectId = try { BuildConfig.FIREBASE_PROJECT_ID } catch (e: Exception) { "" }
                val appId = try { BuildConfig.FIREBASE_APPLICATION_ID } catch (e: Exception) { "" }
                val storageBucket = try { BuildConfig.FIREBASE_STORAGE_BUCKET } catch (e: Exception) { "" }
                val databaseUrl = try { BuildConfig.FIREBASE_DATABASE_URL } catch (e: Exception) { "" }
                val senderId = try { BuildConfig.FIREBASE_MESSAGING_SENDER_ID } catch (e: Exception) { "" }

                if (!apiKey.isNullOrEmpty() && apiKey != "AIzaSy_YOUR_FIREBASE_API_KEY_PLACEHOLDER") {
                    val builder = FirebaseOptions.Builder()
                        .setApiKey(apiKey)
                        .setApplicationId(appId)
                    
                    if (!projectId.isNullOrEmpty()) builder.setProjectId(projectId)
                    if (!storageBucket.isNullOrEmpty()) builder.setStorageBucket(storageBucket)
                    if (!databaseUrl.isNullOrEmpty()) builder.setDatabaseUrl(databaseUrl)
                    if (!senderId.isNullOrEmpty()) builder.setGcmSenderId(senderId)
                    
                    val options = builder.build()
                    FirebaseApp.initializeApp(context, options)
                    Log.d(TAG, "Firebase initialized dynamically with .env configuration.")
                } else {
                    // Try to initialize using the default google-services.json
                    try {
                        FirebaseApp.initializeApp(context)
                        Log.d(TAG, "Firebase initialized via default google-services.json config.")
                    } catch (e: Exception) {
                        Log.w(TAG, "Firebase default auto-init failed. Check Firebase configuration before release.", e)
                    }
                }
            } else {
                Log.d(TAG, "Firebase already initialized.")
            }

            // Configure Firestore settings (enable offline persistence)
            try {
                val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()
                firestore.firestoreSettings = settings
                Log.d(TAG, "Cloud Firestore offline persistence enabled.")
            } catch (e: Exception) {
                Log.w(TAG, "Firestore settings configuration warning: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase Central Service", e)
        }
    }
}

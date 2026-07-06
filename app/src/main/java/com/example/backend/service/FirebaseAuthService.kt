package com.example.backend.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.backend.model.User
import com.example.config.FirebaseConfig

/**
 * Service to manage Firebase Authentication and User metadata in Cloud Firestore.
 */
class FirebaseAuthService(
    private val auth: FirebaseAuth = FirebaseService.auth,
    private val db: FirebaseFirestore = FirebaseService.firestore
) {
    private val TAG = "FirebaseAuthService"

    /**
     * Authenticate an existing user with email and password.
     */
    suspend fun login(email: String, password: String): ResourceState<User> {
        return try {
            Log.d(TAG, "Attempting login for email: $email")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Fetch full User profile from Firestore
                val userDoc = db.collection(FirebaseConfig.Collections.USERS)
                    .document(firebaseUser.uid).get().await()
                
                var userProfile = userDoc.toObject(User::class.java)
                
                if (userProfile == null) {
                    // Fallback profile if Firestore doc doesn't exist yet
                    userProfile = User(
                        uid = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Employee",
                        email = firebaseUser.email ?: email,
                        role = "Staff"
                    )
                    // Create Firestore record
                    db.collection(FirebaseConfig.Collections.USERS)
                        .document(firebaseUser.uid).set(userProfile).await()
                } else {
                    // Update last login timestamp
                    val updatedProfile = userProfile.copy(lastLoginAt = System.currentTimeMillis())
                    db.collection(FirebaseConfig.Collections.USERS)
                        .document(firebaseUser.uid).set(updatedProfile).await()
                    userProfile = updatedProfile
                }

                ResourceState.Success(userProfile, "Login successful. Welcome back, ${userProfile.name}!")
            } else {
                ResourceState.Error(Exception("Auth user is null"), "Failed to obtain user session from Firebase.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            ResourceState.Error(e, "Authentication failed: ${e.localizedMessage ?: "Invalid credentials"}")
        }
    }

    /**
     * Register a new user and create their Firestore metadata.
     */
    suspend fun register(email: String, password: String, name: String, role: String, companyId: String = ""): ResourceState<User> {
        return try {
            Log.d(TAG, "Attempting registration for email: $email, role: $role")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val newUser = User(
                    uid = firebaseUser.uid,
                    name = name,
                    email = email,
                    role = role,
                    companyId = companyId,
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis()
                )
                
                // Write user metadata to Firestore
                db.collection(FirebaseConfig.Collections.USERS)
                    .document(firebaseUser.uid).set(newUser).await()
                
                ResourceState.Success(newUser, "Registration successful for user ${newUser.name} with role ${newUser.role}.")
            } else {
                ResourceState.Error(Exception("Registration returned null user"), "Failed to instantiate Firebase User.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            ResourceState.Error(e, "Registration failed: ${e.localizedMessage ?: "Please try again"}")
        }
    }

    /**
     * Log out current user session.
     */
    fun logout() {
        Log.d(TAG, "Logging out current user session")
        auth.signOut()
    }

    /**
     * Get currently logged-in user profile from Firestore.
     */
    suspend fun getCurrentUserProfile(): ResourceState<User?> {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val userDoc = db.collection(FirebaseConfig.Collections.USERS)
                    .document(firebaseUser.uid).get().await()
                val profile = userDoc.toObject(User::class.java)
                ResourceState.Success(profile, "User profile loaded successfully.")
            } else {
                ResourceState.Success(null, "No active user session found.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load current user profile", e)
            ResourceState.Error(e, "Error loading session profile: ${e.localizedMessage}")
        }
    }
}

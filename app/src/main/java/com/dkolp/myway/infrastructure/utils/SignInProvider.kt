package com.dkolp.myway.infrastructure.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class SignInProvider @Inject constructor(private val supabase: SupabaseClient) {
    val sessionStatusFlow: StateFlow<SessionStatus> = supabase.auth.sessionStatus

    suspend fun login(context: Context) {
        val credentialManager = CredentialManager.create(context)

        val rawNonce = getRawNonce()
        val googleSignInOptions = getGetGoogleIdOption(rawNonce)
        val request = GetCredentialRequest.Builder().addCredentialOption(googleSignInOptions).build()

        val result = credentialManager.getCredential(context, request)
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
        val googleIdToken = googleIdTokenCredential.idToken

        supabase.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
            nonce = rawNonce
        }
    }

    suspend fun logout() {
        supabase.auth.signOut(SignOutScope.LOCAL)
    }

    private val serverClientId = "393488318221-fqrfi1djquse4iirgrnca37t3ha1jk3t.apps.googleusercontent.com"

    private fun getRawNonce(): String {
        return UUID.randomUUID().toString()
    }

    private fun getGetGoogleIdOption(rawNonce: String): GetGoogleIdOption {
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setNonce(hashedNonce)
            .build()
    }
}

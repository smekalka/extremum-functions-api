package io.extremum.functions.api.keycloak

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service
internal class CredentialService(
    @Value("\${extremum.functions.api.xAppId}")
    private val xAppId: String,
    private val keycloakService: KeycloakService,
) {
    private var headers: Map<String, String>? = null

    suspend fun getHeaders(): Map<String, String> {
        headers?.let { return it }

        val jwtToken = keycloakService.getJwtToken()
        val result = mapOf(
            HttpHeaders.AUTHORIZATION to "Bearer $jwtToken",
            "x-app-id" to xAppId,
        )
        headers = result
        return result
    }
}
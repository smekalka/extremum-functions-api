package io.extremum.functions.api.keycloak

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.HttpHeaders

@ExtendWith(MockitoExtension::class)
class CredentialServiceTest {

    private lateinit var credentialService: CredentialService

    @Mock(lenient = true)
    private lateinit var keycloakService: KeycloakService

    @BeforeEach
    fun before() {
        credentialService = CredentialService(
            xAppId = X_APP_ID,
            keycloakService = keycloakService
        )
    }

    @Test
    fun getHeaders() {
        runBlocking {
            val jwtToken = "abc"
            whenever(keycloakService.getJwtToken()).thenReturn(jwtToken)

            val result = credentialService.getHeaders()

            val exp = mapOf(
                HttpHeaders.AUTHORIZATION to "Bearer $jwtToken",
                "x-app-id" to X_APP_ID,
            )
            assertEquals(exp, result)
        }
    }

    @Test
    fun `getHeaders from cache`() {
        runBlocking {
            val jwtToken = "abc"
            whenever(keycloakService.getJwtToken()).thenReturn(jwtToken)
            credentialService.getHeaders()

            whenever(keycloakService.getJwtToken()).thenReturn("newJwtToken")

            val result = credentialService.getHeaders()

            val exp = mapOf(
                HttpHeaders.AUTHORIZATION to "Bearer $jwtToken",
                "x-app-id" to X_APP_ID,
            )
            assertEquals(exp, result)
        }
    }

    private companion object {
        const val X_APP_ID = "012"
    }
}
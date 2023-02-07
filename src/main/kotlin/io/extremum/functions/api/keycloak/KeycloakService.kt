package io.extremum.functions.api.keycloak

import com.fasterxml.jackson.annotation.JsonProperty
import io.extremum.functions.api.url.UrlsHolder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.logging.Logger

@Service
internal class KeycloakService(
    urlsHolder: UrlsHolder,
    @Value("\${serviceClientId}")
    private val serviceClientId: String,
    @Value("\${serviceClientSecret}")
    private val serviceClientSecret: String,
    @Value("\${keycloakUri}")
    private val keycloakUri: String,
) {

    private val logger = Logger.getLogger(this::class.qualifiedName)

    private val url = urlsHolder.keycloakUrl

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(urlsHolder.keycloakUrl)
        .build()

    suspend fun getJwtToken(): String =
        webClient
            .post()
            .uri(keycloakUri)
            .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
            .bodyValue(
                "client_id=$serviceClientId" +
                        "&client_secret=$serviceClientSecret" +
                        "&username=$USERNAME" +
                        "&password=$PASSWORD" +
                        "&grant_type=password"
            )
            .awaitExchange { response ->
                val statusCode = response.statusCode()
                if (statusCode != HttpStatus.OK) {
                    val illegalStateException =
                        IllegalStateException("Request to keycloak $url$keycloakUri failed with code $statusCode")
                    logger.severe(illegalStateException.message)
                    throw illegalStateException
                }

                val responseBody = response.awaitBody<KeycloakResponse>()
                responseBody.accessToken
            }

    private companion object {
        const val USERNAME = "admin"

        const val PASSWORD = "Passw0rd"
    }

    private data class KeycloakResponse(
        @JsonProperty("access_token")
        val accessToken: String
    )
}
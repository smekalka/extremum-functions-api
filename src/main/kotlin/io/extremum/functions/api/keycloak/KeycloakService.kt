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
    @Value("\${extremum.functions.api.keycloak.get.token.path}")
    private val getTokenPath: String,
    @Value("\${extremum.functions.api.keycloak.serviceClientId}")
    private val serviceClientId: String,
    @Value("\${extremum.functions.api.keycloak.serviceClientSecret}")
    private val serviceClientSecret: String,
    @Value("\${extremum.functions.api.keycloak.username}")
    private val username: String,
    @Value("\${extremum.functions.api.keycloak.password}")
    private val password: String,
) {

    private val logger = Logger.getLogger(this::class.qualifiedName)

    private val url = urlsHolder.keycloakUri

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(urlsHolder.keycloakUri)
        .build()

    suspend fun getJwtToken(): String =
        webClient
            .post()
            .uri(getTokenPath)
            .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
            .bodyValue(
                "client_id=$serviceClientId" +
                        "&client_secret=$serviceClientSecret" +
                        "&username=$username" +
                        "&password=$password" +
                        "&grant_type=password"
            )
            .awaitExchange { response ->
                val responseBody = response.awaitBody<KeycloakResponse>()
                val statusCode = response.statusCode()
                if (statusCode != HttpStatus.OK) {
                    if (responseBody.alerts.isNotEmpty()) {
                        throwExceptionWithLogging("Request to keycloak $url$getTokenPath failed with message '${responseBody.alerts[0].message}' and status code $statusCode")
                    }
                    throwExceptionWithLogging("Request to keycloak $url$getTokenPath failed with code $statusCode")
                }

                responseBody.accessToken ?: run {
                    throwExceptionWithLogging("Response from keycloak $url$getTokenPath has no token in body $responseBody")
                }
            }

    private fun throwExceptionWithLogging(msg: String): Nothing {
        val illegalStateException =
            IllegalStateException(msg)
        logger.severe(illegalStateException.message)
        throw illegalStateException
    }

    private data class KeycloakResponse(
        @JsonProperty("access_token")
        val accessToken: String? = null,
        val alerts: List<Alert> = listOf()
    )

    private data class Alert(
        val message: String
    )
}
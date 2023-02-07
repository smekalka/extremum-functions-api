package io.extremum.functions.api.url

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
internal class UrlsHolder(
    @Value("\${apiBaseUrl}")
    baseUrl: String,
    @Value("\${xAppId}")
    xAppId: String,
) {

    val consulUrl: String = getConsulUrl(baseUrl)

    val keycloakUrl: String = getKeycloakUrl(baseUrl, xAppId)

    private companion object {
        fun getConsulUrl(baseUrl: String): String = baseUrl.replace("://api", "://consul")

        fun getKeycloakUrl(baseUrl: String, xAppId: String): String =
            baseUrl.replace("://api", "://auth.app-$xAppId")
    }
}
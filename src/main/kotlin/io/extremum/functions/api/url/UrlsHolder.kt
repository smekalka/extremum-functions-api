package io.extremum.functions.api.url

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
internal class UrlsHolder(
    @Value("\${extremum.functions.api.keycloak.uri:}")
    keycloakUri: String = "",
    @Value("\${extremum.functions.api.baseUrl:}")
    final val baseUrl: String = "",
    @Value("\${extremum.functions.api.xAppId}")
    xAppId: String,
) {

    val keycloakUri: String = getKeycloakUri(keycloakUri, baseUrl, xAppId)

    private companion object {
        fun getKeycloakUri(keycloakUri: String, baseUrl: String, xAppId: String): String =
            keycloakUri.ifEmpty {
                baseUrl.replace("://api", "://auth.app-$xAppId")
            }
    }
}
package io.extremum.functions.api.url

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UrlsHolderTest {

    @Test
    fun keycloak() {
        val withHttps = UrlsHolder(baseUrl = "https://api.ajev84.y.extremum.io", xAppId = "123").keycloakUri
        assertThat(withHttps).isEqualTo("https://auth.app-123.ajev84.y.extremum.io")

        val withHttp = UrlsHolder(baseUrl = "http://api.ajev84.y.extremum.io", xAppId = "123").keycloakUri
        assertThat(withHttp).isEqualTo("http://auth.app-123.ajev84.y.extremum.io")

        val withKeycloakUri = UrlsHolder(keycloakUri = "http://my.compony.org", baseUrl = "any", xAppId = "any").keycloakUri
        assertThat(withKeycloakUri).isEqualTo("http://my.compony.org")
    }
}
package io.extremum.functions.api.url

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UrlsHolderTest {

    @Test
    fun consul() {
        val withHttps = UrlsHolder("https://api.ajev84.y.extremum.io", xAppId = "any").consulUrl
        assertThat(withHttps).isEqualTo("https://consul.ajev84.y.extremum.io")

        val withHttp = UrlsHolder("http://api.ajev84.y.extremum.io", xAppId = "any").consulUrl
        assertThat(withHttp).isEqualTo("http://consul.ajev84.y.extremum.io")
    }

    @Test
    fun keycloak() {
        val withHttps = UrlsHolder("https://api.ajev84.y.extremum.io", "123").keycloakUrl
        assertThat(withHttps).isEqualTo("https://auth.app-123.ajev84.y.extremum.io")

        val withHttp = UrlsHolder("http://api.ajev84.y.extremum.io", "123").keycloakUrl
        assertThat(withHttp).isEqualTo("http://auth.app-123.ajev84.y.extremum.io")
    }
}
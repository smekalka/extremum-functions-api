package io.extremum.functions.api.keycloak

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class KeycloakServiceIT {

    @Autowired
    private lateinit var keycloakService: KeycloakService

    @Disabled("For manual launch only. Test sends real request")
    @Test
    fun getJwtToken() {
        runBlocking {
            val result = keycloakService.getJwtToken()
            println("jwtToken: $result")
            assertThat(result).isNotNull
        }
    }
}
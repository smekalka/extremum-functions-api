package io.extremum.functions.api.triggerTable

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TriggerTableFetcherIT {

    @Autowired
    private lateinit var triggerTableFetcher: TriggerTableFetcher

    @Disabled("For manual launch only. Test sends real request")
    @Test
    fun getTriggerTableString() {
        runBlocking {
            val result = triggerTableFetcher.getTriggerTableString()
            println("result: $result")
            assertThat(result).isNotNull
        }
    }
}
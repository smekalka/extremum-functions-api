package io.extremum.functions.api.triggertable

import com.fasterxml.jackson.annotation.JsonProperty
import io.extremum.functions.api.url.UrlsHolder
import io.extremum.functions.api.function.util.Base64Decoder.base64Decode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.logging.Logger

@Service
internal class TriggerTableFetcher(
    urlsHolder: UrlsHolder,
    @Value("\${extremum.functions.api.consul.trigger.table.path}")
    private val triggerTablePath: String,
) {

    private val logger = Logger.getLogger(this::class.qualifiedName)

    private val uri = urlsHolder.consulUri

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(uri)
        .build()

    suspend fun getTriggerTableString(): String =
        webClient
            .get()
            .uri(triggerTablePath)
            .awaitExchange { response ->
                val statusCode = response.statusCode()
                if (statusCode != HttpStatus.OK) {
                    val illegalStateException =
                        IllegalStateException("Request to consul for trigger table $uri$triggerTablePath failed with code $statusCode")
                    logger.severe(illegalStateException.message)
                    throw illegalStateException
                }
                val responseBody = response.awaitBody<List<TriggerTableResponse>>()
                logger.info("Response status code: $statusCode with body $responseBody")
                responseBody.first().value.base64Decode()
            }

    private data class TriggerTableResponse(
        @JsonProperty("Value")
        val value: String
    )
}
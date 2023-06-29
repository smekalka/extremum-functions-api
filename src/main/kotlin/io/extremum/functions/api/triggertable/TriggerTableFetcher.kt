package io.extremum.functions.api.triggertable

import io.extremum.functions.api.url.UrlsHolder
import io.extremum.functions.api.keycloak.CredentialService
import io.extremum.model.tools.mapper.MapperUtils.convertValue
import io.extremum.sharedmodels.dto.Response
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
    private val credentialService: CredentialService,
) {

    private val logger = Logger.getLogger(this::class.qualifiedName)

    private val uri = urlsHolder.baseUrl

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(uri)
        .build()

    suspend fun getTriggerTableMap(): Map<String, List<String>> {
        val headers = credentialService.getHeaders()
        return webClient
            .get()
            .uri(triggerTablePath)
            .apply {
                headers.forEach { (name, value) ->
                    this.header(name, value)
                }
            }
            .awaitExchange { response ->
                val statusCode = response.statusCode()
                if (statusCode != HttpStatus.OK) {
                    val illegalStateException =
                        IllegalStateException("Request for trigger table $uri$triggerTablePath failed with code $statusCode")
                    logger.severe(illegalStateException.message)
                    throw illegalStateException
                }
                val responseBody = response.awaitBody<Response>()
                logger.info("Response status code: $statusCode with body $responseBody")
                responseBody.result.convertValue()
            }
    }
}
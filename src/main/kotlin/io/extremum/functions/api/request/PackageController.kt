package io.extremum.functions.api.request

import io.extremum.sharedmodels.dto.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

@RestController
class PackageController {

    @Autowired
    private lateinit var requestRouter: RequestRouter

    private val logger = Logger.getLogger(this::class.qualifiedName)

    @PostMapping("/")
    suspend fun call(@RequestBody body: Any): Response {
        logger.info("Received\n\n$body")
        val result = requestRouter.onRequest(body)
        return Response.ok(result)
    }
}
package io.extremum.functions.api.request

import io.extremum.functions.api.exception.ArgumentValidationException
import io.extremum.functions.api.function.FunctionsService
import io.extremum.functions.api.function.MapFunction
import io.extremum.functions.api.function.model.Context
import io.extremum.functions.api.request.model.CallFunctionBody
import io.extremum.model.tools.mapper.MapperUtils.convertToMap
import io.extremum.test.tools.ToJsonFormatter.toJson
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders

@SpringBootTest
class PackageControllerCallMethodIT {

    @Autowired
    private lateinit var packageController: PackageController

    @Autowired
    private lateinit var functionsService: FunctionsService

    @Autowired
    private lateinit var mapFunction: MapFunction

    private val parameters = mapOf(
        "paramName" to 21,
        "nested" to mapOf(
            "subName" to "sub name value"
        )
    )

    private val token = "t"

    private fun body(packageName: String, functionName: String, params: Any = parameters): CallFunctionBody =
        CallFunctionBody(
            parameters = params,
            context = Context(
                headers = mapOf(HttpHeaders.AUTHORIZATION to token),
                function = functionName,
                `package` = packageName
            )
        )

    @Test
    fun `call function with map`() {
        runBlocking {
            val packageName = functionsService.getPackageName()
            val testValue = 22271
            mapFunction.value = testValue
            val result = packageController.call(body(packageName, "map-f").convertToMap())
            assertThat(result.result).isEqualTo(parameters + ("value" to testValue))

            val context = mapFunction.context
            assertThat(context)
                .hasFieldOrPropertyWithValue(Context::headers.name, mapOf(HttpHeaders.AUTHORIZATION to token))
        }
    }

    @Test
    fun `call function with string`() {
        runBlocking {
            val packageName = functionsService.getPackageName()
            val testValue = 22271
            mapFunction.value = testValue
            val result = packageController.call(body(packageName, "map-f", parameters.toJson()).convertToMap())
            assertThat(result.result).isEqualTo(parameters + ("value" to testValue))

            val context = mapFunction.context
            assertThat(context)
                .hasFieldOrPropertyWithValue(Context::headers.name, mapOf(HttpHeaders.AUTHORIZATION to token))
        }
    }

    @Test
    fun `unsupported package`() {
        runBlocking {
            val message = assertThrows<ArgumentValidationException> {
                packageController.call(body("otherPackage", "map-f").convertToMap())
            }.message ?: ""
            assertTrue(message.contains("Unsupported package name"), "Wrong alert message: $message")
        }
    }

    @Test
    fun `unsupported function`() {
        runBlocking {
            val packageName = functionsService.getPackageName()
            val message = assertThrows<ArgumentValidationException> {
                packageController.call(body(packageName, "otherFunction").convertToMap())
            }.message ?: ""
            assertTrue(message.contains("Unsupported function with name"), "Wrong alert message: $message")
        }
    }

    @Test
    fun `method with custom class parameters`() {
        runBlocking {
            val packageName = functionsService.getPackageName()
            val result = packageController.call(body(packageName, "obj-f").convertToMap())
            assertThat(result.result).isEqualTo("sub name value")
        }
    }

    @Test
    fun `method with list values`() {
        runBlocking {
            val packageName = functionsService.getPackageName()
            val result = packageController.call(body(packageName, "list-f", params = listOf(1, 2, 3)).convertToMap())
            assertThat(result.result).isEqualTo(6)
        }
    }

    @Test
    fun `method with wrong parameter type`() {
        runBlocking {
            val packageName = functionsService.getPackageName()
            val message = assertThrows<ArgumentValidationException> {
                packageController.call(body(packageName, "wrong-f", "paramsValueStr").convertToMap())
            }.message ?: ""
            assertTrue(
                message.contains("Can't convert ") && message.contains("as parameter for method"),
                "Wrong alert message: $message"
            )
        }
    }

    @Test
    fun `method with wrong cast`() {
        runBlocking {
            val packageName = functionsService.getPackageName()
            val message = assertThrows<ArgumentValidationException> {
                packageController.call(
                    body(
                        packageName, "list-f", params = mapOf(
                            "notExistField" to listOf(1, 2, 3)
                        )
                    ).convertToMap()
                )
            }.message ?: ""
            assertTrue(message.contains("Cannot deserialize value of type"), "Wrong alert message: $message")
        }
    }

    @Test
    fun `not CallFunctionBody`() {
        runBlocking {
            val message = assertThrows<ArgumentValidationException> {
                packageController.call(
                    mapOf(
                        "notExistField" to listOf(1, 2, 3)
                    )
                )
            }.message ?: ""
            assertTrue(message.contains("Can't convert to CallFunctionBody"), "Wrong alert message: $message")
        }
    }
}


package io.extremum.functions.api.function.util

import io.extremum.functions.api.exception.ArgumentValidationException
import io.extremum.model.tools.mapper.MapperUtils.convertValue
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal object MapperUtils {
    private val logger = Logger.getLogger(this::class.qualifiedName)

    fun Any.convertParams(targetType: KClass<*>, method: KFunction<*>): Any {
        try {
            return this.convertValue(targetType.java)
        } catch (e: Exception) {
            val argumentValidationException =
                ArgumentValidationException("Can't convert $this\nto $targetType\nas parameter for method '$method':\n${e.message}")
            logger.severe(argumentValidationException.message)
            throw argumentValidationException
        }
    }
}
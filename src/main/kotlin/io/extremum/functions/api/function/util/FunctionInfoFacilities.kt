package io.extremum.functions.api.function.util

import io.extremum.common.annotation.function.Function
import io.extremum.common.annotation.function.FunctionContext
import io.extremum.common.annotation.function.FunctionMethod
import io.extremum.common.annotation.function.FunctionPackage
import io.extremum.functions.api.function.BasePackageFunction
import io.extremum.functions.api.function.model.Context
import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.functions.api.function.model.StorageTriggerParameters
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.MergedAnnotation
import org.springframework.core.annotation.MergedAnnotations
import org.springframework.stereotype.Service
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.javaType
import kotlin.reflect.jvm.isAccessible

@Service
internal class FunctionInfoFacilities(
    private val applicationContext: ApplicationContext,
) {

    fun getFunctionInfo(function: BasePackageFunction): FunctionInfo {
        val methods = function::class.functions
        val (method, methodParameterType) = getFunctionMethodInfo(function, methods)
        val onStorageTriggerMethod = getMethodByNameAndParameterType(
            function,
            methods,
            methodName = "onStorageTrigger",
            parameterType = StorageTriggerParameters::class
        )
        val onSignalMethod = getMethodByNameAndParameterType(
            function,
            methods,
            methodName = "onSignal",
            parameterType = SignalParameters::class
        )
        return FunctionInfo(
            function,
            getContextField(function),
            method,
            methodParameterType,
            onStorageTriggerMethod,
            onSignalMethod
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getFunctionMethodInfo(
        function: BasePackageFunction,
        methods: Collection<KFunction<*>>
    ): Pair<KFunction<*>, KClass<*>> {
        val method = methods.find { it.hasAnnotation<FunctionMethod>() }
            ?: throw IllegalStateException(
                "Function ${function.javaClass} has no method with annotation FunctionMethod"
            )
        val parameters = method.parameters
        // метод с одним параметром - это метод с 2 KParameter, первый из которых имеет тип самого класса-функции
        if (parameters.size != 2 || parameters[0].type.javaType != function::class.java) {
            throw IllegalStateException("Method $method must have single parameter")
        }
        method.isAccessible = true
        return method to parameters[1].type.classifier as KClass<*>
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getMethodByNameAndParameterType(
        function: BasePackageFunction,
        methods: Collection<KFunction<*>>,
        methodName: String,
        parameterType: KClass<*>
    ): KFunction<*> =
        methods.find { it.name == methodName && it.parameters[1].type.javaType == parameterType.java }
            ?: throw IllegalStateException(
                "Function ${function.javaClass} has no method '$methodName' with parameter $parameterType"
            )

    private fun getContextField(function: BasePackageFunction): Field {
        val fields = function.javaClass.declaredFields
        val contextField = fields.find { it.isAnnotationPresent(FunctionContext::class.java) }
            ?: throw IllegalStateException("Function ${function.javaClass} has no field with annotation FunctionContext")
        if (contextField.type != Context::class.java) {
            throw IllegalStateException("Field $contextField must have io.extremum.functions.api.model.Context type")
        }
        contextField.isAccessible = true
        return contextField
    }

    fun getPackageName(): String {
        val beanWithFunctionPackageAnnotation =
            applicationContext.getBeansWithAnnotation(FunctionPackage::class.java)
                .values.firstOrNull()
                ?: throw IllegalStateException("Can't find bean with annotation FunctionPackage")
        val mergedFunctionPackageAnnotation = MergedAnnotations.from(
            beanWithFunctionPackageAnnotation::class.java,
            MergedAnnotations.SearchStrategy.TYPE_HIERARCHY
        ).get(FunctionPackage::class.java)
        val functionPackageAnnotation =
            mergedFunctionPackageAnnotation.synthesize(MergedAnnotation<FunctionPackage>::isPresent)
                .get()
        return functionPackageAnnotation.name.validateName()
    }

    private val validNameRegex = "[a-z][-a-z0-9]{1,61}[a-z0-9]".toRegex()

    private fun String.validateName(): String {
        if (!this.matches(validNameRegex)) {
            throw IllegalStateException("Name '$this' does not match the pattern '$validNameRegex'")
        }
        return this
    }

    fun getFunctionsBeans(): MutableCollection<BasePackageFunction> =
        applicationContext.getBeansOfType(BasePackageFunction::class.java).values

    fun getFunctionName(function: BasePackageFunction): String {
        val functionAnnotation = function::class.java.getAnnotation(Function::class.java)
            ?: throw IllegalStateException("Function ${function::class.java.name} has no annotation Function")
        return functionAnnotation.name.validateName()
    }

    fun checkDuplicatedFunctionNames(
        functionsBeans: MutableCollection<BasePackageFunction>,
        functions: Map<String, FunctionInfo>
    ) {
        if (functions.size != functionsBeans.size) {
            val functionsWithDuplicatedNames = functionsBeans.map { function ->
                getFunctionName(function) to function::class.java.name
            }.groupBy { it.first }
                .filter { it.value.size > 1 }
            if (functionsWithDuplicatedNames.isNotEmpty()) {
                val duplicatedNamesMsg = functionsWithDuplicatedNames.map { functionWithDuplicatedName ->
                    "name '${functionWithDuplicatedName.key}': ${functionWithDuplicatedName.value.map { it.second }}"
                }
                throw IllegalStateException("Duplicated names of functions:\n${duplicatedNamesMsg.joinToString(";\n")}")
            }
        }
    }

    /**
     * Информация о классе-функции
     */
    data class FunctionInfo(
        val function: BasePackageFunction,
        val contextField: Field,
        /** Основной метод функции - метод для прямого вызова */
        val method: KFunction<*>,
        /** Тип параметра основного метода */
        val methodParameterType: KClass<*>,
        val onStorageTriggerMethod: KFunction<*>,
        val onSignalMethod: KFunction<*>,
    )
}
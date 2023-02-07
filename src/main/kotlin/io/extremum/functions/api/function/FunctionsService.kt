package io.extremum.functions.api.function

import io.extremum.functions.api.exception.UnsupportedFunctionNameException
import io.extremum.functions.api.exception.UnsupportedPackageNameException
import io.extremum.functions.api.function.model.Context
import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.functions.api.function.model.StorageTriggerParameters
import io.extremum.functions.api.function.util.FunctionInfoFacilities
import io.extremum.functions.api.function.util.FunctionInfoFacilities.FunctionInfo
import io.extremum.functions.api.function.util.MapperUtils.convertParams
import io.extremum.functions.api.keycloak.CredentialService
import org.springframework.stereotype.Service
import java.util.logging.Logger
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.functions

@Service
internal class FunctionsService(
    functionInfoFacilities: FunctionInfoFacilities,
    private val credentialService: CredentialService,
) {

    private final val packageName: String

    /**
     * Мапа "имя функции" - "функция и ее информация"
     */
    private lateinit var functions: Map<String, FunctionInfo>

    /**
     * Имена функций (ключи из [functions])
     */
    private lateinit var functionNames: Set<String>

    private val logger = Logger.getLogger(this::class.qualifiedName)

    init {
        packageName = functionInfoFacilities.getPackageName()
        val functionsBeans = functionInfoFacilities.getFunctionsBeans()
        functions = functionsBeans.associate { function ->
            functionInfoFacilities.getFunctionName(function) to functionInfoFacilities.getFunctionInfo(function)
        }
        functionInfoFacilities.checkDuplicatedFunctionNames(functionsBeans, functions)
        functionNames = functions.keys
        logger.info("Functions for package '$packageName' have been initialized. Found ${functions.size} functions: $functionNames.")
    }

    fun getPackageName(): String = packageName

    suspend fun callMethod(packageName: String, functionName: String, context: Context, params: Any?): Any? {
        if (this.packageName != packageName) {
            throw UnsupportedPackageNameException(packageName = packageName, availablePackageName = this.packageName)
        }

        val (function, contextField, method, methodParameterType) = getFunction(functionName)
        logger.info("Call $packageName.$functionName.${method.name}")

        contextField.set(function, context)

        val convertedParams = params?.convertParams(targetType = methodParameterType, method)
        return if (method.isSuspend) {
            method.callSuspend(function, convertedParams)
        } else {
            method.call(function, convertedParams)
        }
    }

    suspend fun onStorageTrigger(functionName: String, params: StorageTriggerParameters) {
        val functionsInfo = getFunctionNoThrow(functionName) ?: return
        setCredentialContextForTrigger(functionName, functionsInfo)
        functionsInfo.onStorageTriggerMethod.callSuspend(functionsInfo.function, params)
    }

    suspend fun onSignal(functionName: String, params: SignalParameters) {
        val functionsInfo = getFunctionNoThrow(functionName) ?: return
        setCredentialContextForTrigger(functionName, functionsInfo)
        functionsInfo.onSignalMethod.callSuspend(functionsInfo.function, params)
    }

    private suspend fun setCredentialContextForTrigger(
        functionName: String,
        functionsInfo: FunctionInfo
    ) {
        val context = Context(
            headers = credentialService.getHeaders(),
            `package` = packageName,
            function = functionName
        )
        with(functionsInfo) {
            contextField.set(function, context)
        }
    }

    private fun getFunctionNoThrow(functionName: String): FunctionInfo? = functions[functionName] ?: kotlin.run {
        logger.warning("Unsupported function with name $functionName. Available: $functionNames.")
        null
    }

    private fun getFunction(functionName: String): FunctionInfo = functions[functionName]
        ?: throw UnsupportedFunctionNameException(functionName, functionNames)
}

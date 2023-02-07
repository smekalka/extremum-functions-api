package io.extremum.functions.api.exception

class UnsupportedFunctionNameException(functionName: String, functionNames: Set<String>) :
    ArgumentValidationException("Unsupported function with name $functionName. Available: $functionNames.")
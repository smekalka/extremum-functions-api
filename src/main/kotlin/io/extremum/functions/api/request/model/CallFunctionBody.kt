package io.extremum.functions.api.request.model

import io.extremum.functions.api.function.model.Context

data class CallFunctionBody(
    val parameters: Any? = null,
    val context: Context,
)
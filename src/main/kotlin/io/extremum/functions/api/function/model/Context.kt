package io.extremum.functions.api.function.model

data class Context(
    val headers: Map<String, String>,
    /** Название пакета */
    val `package`: String,
    /** Название функции */
    val function: String,
) {
    companion object {
        val EMPTY = Context(
            headers = mapOf(),
            function = "",
            `package` = ""
        )
    }
}
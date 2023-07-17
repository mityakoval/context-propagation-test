package no.vipps.login.contextpropagationtest.logging

enum class LogKey(private val description: String) {
    event("Vipps event identification"),
    requestId("Unique id of the request"),
    requestMethod("HTTP method for the request"),
    requestURI("URI of the incoming request"),
    responseStatus("HTTP status of the response");

    fun description(): String {
        return description
    }

    companion object {
        /**
         * Default value to use when a key is used for MDC.
         */
        const val DEFAULT_MDC_VALUE = "undefined_mdc_value"
    }
}

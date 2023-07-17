package no.vipps.login.contextpropagationtest.logging

import io.micrometer.context.ContextRegistry
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration

val contextRegistry = ContextRegistry()

const val MDC_CONTAINER_CONTEXT_KEY = "mdc_container"

@Configuration
class MdcManager(
    val mdcKeys: List<LogKey> = listOf(
        LogKey.requestMethod, LogKey.requestURI, LogKey.requestId
    )
) {

    init {
        contextRegistry.registerThreadLocalAccessor(
            MDC_CONTAINER_CONTEXT_KEY,
            {
                MdcContainer(
                    mdcKeys.associate {
                        it.toString() to MDC.get(it.toString())
                    }
                )
            },
            { mdcContainer ->
                mdcContainer.mdc
                    .forEach { (key, value) ->
                        MDC.put(key, value)
                    }
            },
            {
                mdcKeys.forEach {
                    MDC.remove(it.toString())
                }
            }
        )
    }
}

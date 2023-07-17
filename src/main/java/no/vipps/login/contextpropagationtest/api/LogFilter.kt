package no.vipps.login.contextpropagationtest.api

import mu.KotlinLogging
import no.vipps.login.contextpropagationtest.logging.*
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context

private val logger = KotlinLogging.logger {}

const val MDC_CONTAINER_CONTEXT_KEY = "mdc_container"

@Component
class LogFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {


        contextRegistry.registerThreadLocalAccessor(
            MDC_CONTAINER_CONTEXT_KEY,
            {
                MdcContainer(
                    mapOf(
                        LogKey.requestId.toString() to MDC.get(LogKey.requestId.toString()),
                        LogKey.requestMethod.toString() to MDC.get(LogKey.requestMethod.toString()),
                        LogKey.requestURI.toString() to MDC.get(LogKey.requestURI.toString())
                    )
                )
            },
            { mdcContainer ->
                MDC.put(LogKey.requestId.toString(), mdcContainer.mdc[LogKey.requestId.toString()])
            },
            {
                MDC.clear()
            }
        )

        setMdc(
            LogKey.requestURI to exchange.request.uri.toString(),
            LogKey.requestMethod to exchange.request.method.name(),
            LogKey.requestId to exchange.request.id
        )

        return chain.filter(exchange)
            .contextWrite(Context.of(MDC_CONTAINER_CONTEXT_KEY, MdcContainer(MDC.getCopyOfContextMap())))
            .doFinally {
                logWithEvent(
                    LogEvent.AUDIT_HTTP_RESPONSE,
                    LogKey.responseStatus to exchange.response.statusCode!!.value().toString()
                ) {
                    logger.info { "Http response status" }
                }
                MDC.clear()
            }
    }
}

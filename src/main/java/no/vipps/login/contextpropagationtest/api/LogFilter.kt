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
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class LogFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        setMdc(
            LogKey.requestURI to exchange.request.uri.toString(),
            LogKey.requestMethod to exchange.request.method.name(),
            LogKey.requestId to UUID.randomUUID().toString()
        )

        val mdcContext = Context.of(MDC_CONTAINER_CONTEXT_KEY, MdcContainer(MDC.getCopyOfContextMap()))

        return chain.filter(exchange)
            .doFinally {
                logWithEvent(
                    LogEvent.AUDIT_HTTP_RESPONSE,
                    LogKey.responseStatus to exchange.response.statusCode!!.value().toString()
                ) {
                    logger.info { "Http response status" }
                }
            }.contextWrite(mdcContext)
    }
}

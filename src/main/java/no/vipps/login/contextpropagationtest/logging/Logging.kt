package no.vipps.login.contextpropagationtest.logging

import io.micrometer.context.ContextRegistry
import mu.withLoggingContext
import org.slf4j.MDC

val contextRegistry = ContextRegistry()

fun <T> logWithEvent(event: LogEvent, body: () -> T): T {
    return withLoggingContext(LogKey.event.toString() to event.event, body = body)
}

fun <T> logWithEvent(event: LogEvent, vararg pair: Pair<LogKey, Any>, body: () -> T): T {
    val additionalValues = pair.map { it.first.toString() to it.second.toString() }.toTypedArray()
    val valuesToLog = arrayOf(LogKey.event.toString() to event.event, *additionalValues)
    return withLoggingContext(*valuesToLog, body = body)
}

fun <T> logWithData(vararg pair: Pair<LogKey, Any>, body: () -> T): T {
    val valuesToLog = pair.map { it.first.toString() to it.second.toString() }.toTypedArray()
    return withLoggingContext(*valuesToLog, body = body)
}

fun setMdc(vararg pair: Pair<LogKey, Any>) {
    pair.forEach { setMdc(it.first, it.second.toString()) }
}

fun setMdc(logKey: LogKey, value: String) {
    MDC.put(logKey.toString(), value)
}

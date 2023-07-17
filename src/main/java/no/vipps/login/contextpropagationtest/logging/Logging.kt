package no.vipps.login.contextpropagationtest.logging

import io.micrometer.context.ContextSnapshotFactory
import mu.withLoggingContext
import org.slf4j.MDC

fun <T> logWithEvent(event: LogEvent, body: () -> T): T {
    return log(arrayOf(LogKey.event.toString() to event.event), body = body)
}

fun <T> logWithEvent(event: LogEvent, vararg pair: Pair<LogKey, Any>, body: () -> T): T {
    val additionalValues = pair.map { it.first.toString() to it.second.toString() }.toTypedArray()
    val valuesToLog = arrayOf(LogKey.event.toString() to event.event, *additionalValues)
    return log(valuesToLog, body = body)
}

fun <T> logWithData(vararg pair: Pair<LogKey, Any>, body: () -> T): T {
    val valuesToLog = pair.map { it.first.toString() to it.second.toString() }.toTypedArray()
    return log(valuesToLog, body = body)
}

private fun <T> log(valuesToLog: Array<Pair<String, String>>, body: () -> T): T {
    ContextSnapshotFactory.builder()
        .contextRegistry(contextRegistry)
        .build()
        .captureAll()
        .setThreadLocals()
        .use { _ -> return withLoggingContext(*valuesToLog, body = body) }
}

fun setMdc(vararg pair: Pair<LogKey, Any>) {
    pair.forEach { setMdc(it.first, it.second.toString()) }
}

fun setMdc(logKey: LogKey, value: String) {
    MDC.put(logKey.toString(), value)
}

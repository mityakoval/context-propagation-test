package no.vipps.login.contextpropagationtest.logging

import java.util.Locale

const val app = "vipps.user.benefits"

private fun code(eventCategory: EventCategory, groupType: GroupType, name: String): String {
    return "${eventCategory.value()}.$app.${groupType.value()}.$name"
}

private fun code(eventCategory: EventCategory, name: String): String {
    return "${eventCategory.value()}.$app.$name"
}

/**
 * Code format: [audit|suspicious|error].vipps.user.benefits.[optional group].[SPECIFIC EVENT]
 */
enum class LogEvent(val event: String) {
    AUDIT_HTTP_REQUEST_RECEIVED(code(EventCategory.AUDIT, "http_request_received")),
    AUDIT_HTTP_RESPONSE(code(EventCategory.AUDIT, "http_response")),
    AUDIT_GENERAL_EVENT(code(EventCategory.AUDIT, "general_event")),
}

enum class EventCategory {
    INFO,
    WARN,
    AUDIT,
    ERROR,
    SUSPICIOUS;

    fun value(): String {
        return toString().lowercase(Locale.getDefault())
    }
}

enum class SuspiciousGroupType : GroupType {
    AUTHORIZATION,
    INPUT
}

enum class ErrorGroupType : GroupType {
    STORAGE
}

enum class WarnGroupType : GroupType {
    STORAGE
}

interface GroupType {
    fun value(): String {
        return toString().lowercase(Locale.getDefault())
    }
}

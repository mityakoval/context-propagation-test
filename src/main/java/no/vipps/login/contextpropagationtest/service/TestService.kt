package no.vipps.login.contextpropagationtest.service

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import no.vipps.login.contextpropagationtest.logging.LogEvent
import no.vipps.login.contextpropagationtest.logging.logWithData
import no.vipps.login.contextpropagationtest.logging.logWithEvent
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

private val logger = KotlinLogging.logger {}


@Service
class TestService(
    webClientBuilder: WebClient.Builder
) {
    private val webClient: WebClient = webClientBuilder
        .baseUrl("http://localhost:8081")
        .build()

    suspend fun callSlowEndpoint(): String = withContext(MDCContext()){
        logger.info { "Service#callSlowEndpoint" }
        call()
    }

    fun justLogging() {
        logWithEvent(LogEvent.AUDIT_GENERAL_EVENT) {
            logger.info { "Service#justLogging" }
        }
    }

    private suspend fun call(): String {
        return webClient.get()
            .uri("/api/delayedResponse")
            .retrieve()
            .bodyToMono(String::class.java)
//            .doOnNext {
//                logWithEvent(LogEvent.AUDIT_HTTP_RESPONSE) {
//                    logger.info { "Received a response" }
//                }
//            }
            .awaitFirst()
    }
}

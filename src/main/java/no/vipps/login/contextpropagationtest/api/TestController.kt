package no.vipps.login.contextpropagationtest.api

import mu.KotlinLogging
import no.vipps.login.contextpropagationtest.api.model.TestResponse
import no.vipps.login.contextpropagationtest.logging.LogEvent
import no.vipps.login.contextpropagationtest.logging.logWithEvent
import no.vipps.login.contextpropagationtest.service.TestService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("api")
class TestController(
    private val testService: TestService
) {

    @GetMapping(path = ["hello"], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun hello(): ResponseEntity<TestResponse> {

        logWithEvent(LogEvent.AUDIT_HTTP_REQUEST_RECEIVED) {
            logger.info { "Hello World!" }
        }

        testService.callSlowEndpoint()

        return ResponseEntity.ok(TestResponse("Hello World!"))
    }


    @GetMapping(path = ["delayedResponse"])
    fun delayed(): Mono<TestResponse> {
        return Mono.delay(Duration.ofMillis(10))
            .doOnNext {
                logWithEvent(LogEvent.AUDIT_HTTP_RESPONSE) {
                    logger.info { "Delayed response by 100ms" }
                }
            }
            .then(Mono.just(TestResponse("I'm slow :(")))
            .contextCapture()
    }
}

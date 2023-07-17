package no.vipps.login.contextpropagationtest.service

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class TestService(
    webClientBuilder: WebClient.Builder
) {
    private val webClient: WebClient = webClientBuilder
        .baseUrl("http://localhost:8081")
        .build()

    suspend fun callSlowEndpoint(): String = coroutineScope {
        call()
    }

    private suspend fun call(): String {
        return webClient.get()
            .uri("/api/delayedResponse")
            .retrieve()
            .bodyToMono(String::class.java)
            .awaitFirst()
    }
}

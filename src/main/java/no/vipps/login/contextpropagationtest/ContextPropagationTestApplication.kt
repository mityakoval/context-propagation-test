package no.vipps.login.contextpropagationtest

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import reactor.core.publisher.Hooks
import reactor.tools.agent.ReactorDebugAgent

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
@EnableConfigurationProperties
class ContextPropagationTestApplication

fun main(args: Array<String>) {
	Hooks.enableAutomaticContextPropagation()
	ReactorDebugAgent.init()
	SpringApplication.run(ContextPropagationTestApplication::class.java, *args)
}

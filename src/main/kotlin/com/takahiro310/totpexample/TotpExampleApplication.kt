package com.takahiro310.totpexample

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@SpringBootApplication
class TotpExampleApplication {
    @Bean
    @Profile("dev")
    fun init() = CommandLineRunner {
        println("*** CommandLineRunner ***")
    }
}

fun main(args: Array<String>) {
    runApplication<TotpExampleApplication>(*args)
}

package io.extremum.functions.api

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@ComponentScan
@PropertySource("classpath:functions.api.properties")
@ConditionalOnProperty(prefix = "extremum.functions.api", name = ["autoconfiguration"], havingValue = "true", matchIfMissing = true)
class FunctionsApiConfiguration
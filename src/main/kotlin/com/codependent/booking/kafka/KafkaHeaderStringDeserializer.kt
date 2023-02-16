package com.codependent.booking.kafka

import org.apache.camel.component.kafka.serde.KafkaHeaderDeserializer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class KafkaHeaderStringDeserializer : KafkaHeaderDeserializer {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun deserialize(key: String, value: ByteArray): String {
        val stringHeader = String(value)
        logger.debug("key {} - value {}", key, stringHeader)
        return stringHeader
    }
}
package com.codependent.booking.route

import com.codependent.booking.dto.Booking
import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.SagaCompletionMode.MANUAL
import org.apache.camel.model.SagaPropagation.MANDATORY
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.camel.saga.InMemorySagaService
import org.springframework.stereotype.Component

@Component
class BookingRoute (context: CamelContext) : RouteBuilder(context) {

    override fun configure() {

        camelContext.addService(InMemorySagaService())

        rest("/bookings")
            .post("/")
            .to("direct:bookings")

        from("direct:bookings")
            .log("Processing Booking \${body} \${headers}")
            .unmarshal().json(JsonLibrary.Jackson, Booking::class.java)
            .log("Booking Unmarshalled \${body.getClass()} \${body} \${headers}")
            .saga()
            .completionMode(MANUAL)
            .to("direct:bookStep")

        from("direct:bookStep")
            .saga()
            .propagation(MANDATORY)
            .marshal().json(JsonLibrary.Jackson)
            .to("kafka:booking-requests?brokers=localhost:9092")

        //from("kafka:booking-responses?brokers=localhost:9092&headerDeserializer=#kafkaHeaderStringDeserializer")
        from("kafka:booking-responses?brokers=localhost:9092")
            .saga()
            .propagation(MANDATORY)
            .log("Booking response \${body} \${headers}")
            .to("saga:complete")


        from("kafka:booking-requests?brokers=localhost:9092&headerDeserializer=#kafkaHeaderStringDeserializer")
            .log("Booking requested \${body.getClass()} \${body} \${headers}")
            .to("kafka:booking-responses?brokers=localhost:9092")
    }

}
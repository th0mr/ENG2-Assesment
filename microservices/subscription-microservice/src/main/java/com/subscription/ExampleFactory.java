package com.subscription;

import org.apache.kafka.streams.kstream.KStream;

import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Factory
public class ExampleFactory {

    @Singleton
    @Named("example")
    KStream<String, String> exampleStream(ConfiguredStreamBuilder builder) {
        return builder.stream("streams-plaintext-input");
    }
}

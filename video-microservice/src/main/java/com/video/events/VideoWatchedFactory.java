package com.video.events;

import java.util.Properties;

import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import com.video.domain.Video;

import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Factory
public class VideoWatchedFactory {
		
    @Singleton
    KStream<String, Integer> WatchedStream(ConfiguredStreamBuilder builder) {
    	Properties props = builder.getConfiguration();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "video-microservice-application");
    	return builder.stream("video-watched");
    }
}
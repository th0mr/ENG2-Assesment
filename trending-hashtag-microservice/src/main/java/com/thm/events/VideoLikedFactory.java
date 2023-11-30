package com.thm.events;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import com.thm.domain.Hashtag;
import com.thm.domain.Video;

import io.micronaut.configuration.kafka.serde.SerdeRegistry;
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;


@Factory
public class VideoLikedFactory {
	
	@Inject
	SerdeRegistry serdeRegistry;
	
	@Singleton
    KStream<String, Integer> likedDislikedStream(ConfiguredStreamBuilder builder) {
    	
		Properties props = builder.getConfiguration();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "thm-microservice-application");
		
    	KStream<Long, Video> likedVideos = builder.stream("video-liked", Consumed.with(Serdes.Long(), serdeRegistry.getSerde(Video.class)));
    	
		// Define a new stream of Hashtag Name -> Integer (true=1, false=-1)
    	// We are splitting up a single record of a video being liked into many, one for
    	// each hashtag present.
		KStream<String, Integer> hashtagStream = likedVideos.flatMap(
				(key, value) -> {
					List<KeyValue<String, Integer>> result = new LinkedList<>();
					for (Hashtag h : value.getHashtags()){
						result.add(KeyValue.pair(h.getName(), 1));
					}
					return result;
				});
		
		// Send it to the liked-disliked stream
		hashtagStream.to("hashtags-liked-disliked-stream", Produced.with(Serdes.String(), Serdes.Integer()));

        return hashtagStream;
    }
}

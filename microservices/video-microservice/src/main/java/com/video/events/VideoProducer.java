package com.video.events;

import com.video.domain.Video;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface VideoProducer {

	String TOPIC_WATCHED = "video-watched";
	String TOPIC_LIKED = "video-liked";
	String TOPIC_DISLIKED = "video-disliked";
	String TOPIC_POSTED = "video-posted";
	
	@Topic(TOPIC_WATCHED)
	void watchedVideo(@KafkaKey Long userId, Video v);
	
	@Topic(TOPIC_LIKED)
	void likedVideo(@KafkaKey Long userId, Video v);
	
	@Topic(TOPIC_DISLIKED)
	void dislikedVideo(@KafkaKey Long userId, Video v);
	
	@Topic(TOPIC_POSTED)
	void postedVideo(@KafkaKey Long userId, Video v);
}

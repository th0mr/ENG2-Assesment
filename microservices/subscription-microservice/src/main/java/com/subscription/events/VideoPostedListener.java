package com.subscription.events;

import com.subscription.domain.Hashtag;
import com.subscription.domain.Video;
import com.subscription.repositories.SubscriptionRepository;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.inject.Inject;

@KafkaListener(groupId = "video-sm-consumers")
public class VideoPostedListener {
	
	@Inject
	SubscriptionRepository repo;
	
	final String TOPIC_LIKED="video-liked";

	@Topic(TOPIC_LIKED)
	public void likedVideo(@KafkaKey Long id, Video video) {
		System.out.printf("Video liked by userId: %d%n", id);
		
		for(Hashtag hashtag : video.getHashtags()) {
			System.out.println("liked event for hashtag " + hashtag.getName());
		}
	}
}

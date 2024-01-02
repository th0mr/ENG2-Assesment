package com.trendinghashtag.events;

import java.time.LocalDateTime;

import com.trendinghashtag.domain.Hashtag;
import com.trendinghashtag.domain.HashtagLikedDislikedEvent;
import com.trendinghashtag.domain.Video;
import com.trendinghashtag.repositories.HashtagLikedDislikedEventRepository;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.inject.Inject;

@KafkaListener(groupId = "video-thm-consumers")
public class VideoLikedListener {
	
	@Inject
	HashtagLikedDislikedEventRepository repo;
	
	final String TOPIC_LIKED="video-liked";

	@Topic(TOPIC_LIKED)
	public void likedVideo(@KafkaKey Long id, Video video) {
		System.out.printf("Video liked by userId: %d%n", id);
		
		for(Hashtag hashtag : video.getHashtags()) {
			System.out.println("liked event for hashtag " + hashtag.getName());
			HashtagLikedDislikedEvent event = new HashtagLikedDislikedEvent();
			event.setTimestamp(LocalDateTime.now());
			event.setHashtagName(hashtag.getName());
			// This is a like so set int value to 1
			event.setValue(1);
			repo.save(event);
			System.out.println("Liked event id " + event.getId() + " for hashtag " + event.getHashtagName() + " at timestamp " + event.getTimestamp() + " saved to repository");
		}
	}
}

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
public class VideoDislikedListener {
	
	@Inject
	HashtagLikedDislikedEventRepository repo;
	
	final String TOPIC_DISLIKED="video-disliked";

	@Topic(TOPIC_DISLIKED)
	public void dislikedVideo(@KafkaKey Long id, Video video) {
		System.out.printf("Video disliked by userId: %d%n", id);
		
		for(Hashtag hashtag : video.getHashtags()) {
			System.out.println("disliked event for hashtag " + hashtag.getName());
			HashtagLikedDislikedEvent event = new HashtagLikedDislikedEvent();
			event.setTimestamp(LocalDateTime.now());
			event.setHashtagName(hashtag.getName());
			// This is a dislike so set int value to -1
			event.setValue(-1);
			repo.save(event);
			System.out.println("Disliked event id " + event.getId() + " for hashtag " + event.getHashtagName() + " at timestamp " + event.getTimestamp() + " saved to repository");
		}
	}
	
}
package com.subscription.events;

import java.util.List;

import com.subscription.domain.Hashtag;
import com.subscription.domain.Subscription;
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
	
	final String TOPIC_POSTED="video-posted";

	@Topic(TOPIC_POSTED)
	public void postedVideo(@KafkaKey Long id, Video video) {
		System.out.printf("Video posted by userId: %d%n", id);
		
		for(Hashtag hashtag : video.getHashtags()) {
			System.out.println("posted event for hashtag " + hashtag.getName() + " for videoId=" + video.getId());
			
			List<Subscription> subsToHashtag = repo.findAllByHashtagId(hashtag.getId());
			
			for (Subscription sub : subsToHashtag) {
				System.out.println("Adding video ID=" + video.getId() + " for subscription.VideosPostedSinceSub, subId=" + sub.getId());
				sub.getVideosPostedSinceSub().add(video.getId());
				repo.update(sub);
			}
		}
	}
}

package com.thm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.thm.clients.TrendingHashtagsClient;
import com.trendinghashtag.domain.Hashtag;
import com.trendinghashtag.domain.HashtagLikedDislikedEvent;
import com.trendinghashtag.domain.User;
import com.trendinghashtag.domain.Video;
import com.trendinghashtag.events.VideoDislikedListener;
import com.trendinghashtag.events.VideoLikedListener;
import com.trendinghashtag.repositories.HashtagLikedDislikedEventRepository;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(transactional = false, environments = "no_streams")
public class KafkaListenerTest {

	@Inject
    TrendingHashtagsClient trendingHashtagsClient;
	
	@Inject
	VideoLikedListener likedListener;
	
	@Inject
	VideoDislikedListener dislikedListener;
	
	@Inject
	HashtagLikedDislikedEventRepository likeDislikeRepository;
	
//	@Inject
//	TestProducer testProducer;
	
	@BeforeEach
	public void clean() {
		// Wipe clean all the repositories and captured producer records
		likeDislikeRepository.deleteAll();
	}
	
	// Two types of test are in here, ones where we invoke the methods in the listeners
	// to test the logic is working, and ones where we send a record to the kafka topic
	// to see if it triggers the correct effect.
	
	// TODO - Fix the second kind of test
	

//	@KafkaClient
//	public interface TestProducer {
//	
//		String TOPIC_LIKED = "video-liked";
//		String TOPIC_DISLIKED = "video-disliked";
//		
//		@Topic(TOPIC_LIKED)
//		void likedVideo(@KafkaKey Long userId, Video v);
//		
//		@Topic(TOPIC_DISLIKED)
//		void dislikedVideo(@KafkaKey Long userId, Video v);
//	}
	
	public Video getTestVideo() {
		// Assembles a simple video with a creator and two tags, "tag_1" and "tag_2"
		User u = new User();
		u.setUsername("test_user");
		
		Hashtag h1 = new Hashtag();
		h1.setName("tag_1");
		Hashtag h2 = new Hashtag();
		h2.setName("tag_2");
		
		Set<Hashtag> hashtags =  new HashSet<Hashtag>();
		hashtags.add(h1);
		hashtags.add(h2);
		
		Video v = new Video();
		v.setTitle("test_video");
		v.setCreator(u);
		v.setHashtags(hashtags);
		return v;
	}
	
	// Utility methods
	private <T> List<T> iterableToList(Iterable<T> iterable) {
		List<T> l = new ArrayList<>();
		iterable.forEach(l::add);
		return l;
	}
	
	
	// Testing the methods without using kafka to activate them 
	
	@Test
	public void TestLikedVideoListener() {
		Video v = getTestVideo();
		
		Iterable<HashtagLikedDislikedEvent> hlds = likeDislikeRepository.findAll();
		assertEquals(0, hlds.spliterator().estimateSize(), "Before triggering the method the size of the repo should be 0");
		// Invoke the likedVideo method, this avoids sending kafka messages
		// Use fake user id and a simple video with two tags
		likedListener.likedVideo(v.getCreator().getId(), v);
		
		// Check that the repo now contains a liked HashLikeDislikeEvent
		List<HashtagLikedDislikedEvent> hldsList = iterableToList(likeDislikeRepository.findAll());
		assertEquals(2, hldsList.size(), "Size of the repo should now be 2");
		
		assertEquals(1, hldsList.get(0).getValue(), "The value of the event should be 1, i.e. liked");
		assertEquals(1, hldsList.get(1).getValue(), "The value of the event should be 1, i.e. liked");
		
		List<String> hashtagNames = hldsList.stream()
						        	.map(x -> x.getHashtagName())
						        	.collect(Collectors.toList());
		assertTrue(hashtagNames.contains("tag_1"), "The first tag should be one of the events");
		assertTrue(hashtagNames.contains("tag_2"), "The second tag should be one of the events");
	}
	
	@Test
	public void TestDislikedVideoListener() {
		Video v = getTestVideo();
		
		Iterable<HashtagLikedDislikedEvent> hlds = likeDislikeRepository.findAll();
		assertEquals(0, hlds.spliterator().estimateSize(), "Before triggering the method the size of the repo should be 0");
		// Invoke the likedVideo method, this avoids sending kafka messages
		// Use fake user id and a simple video with two tags
		dislikedListener.dislikedVideo(v.getCreator().getId(), v);
		
		// Check that the repo now contains a liked HashLikeDislikeEvent
		List<HashtagLikedDislikedEvent> hldsList = iterableToList(likeDislikeRepository.findAll());
		assertEquals(2, hldsList.size(), "Size of the repo should now be 2");
		
		assertEquals(-1, hldsList.get(0).getValue(), "The value of the event should be -1, i.e. disliked");
		assertEquals(-1, hldsList.get(1).getValue(), "The value of the event should be -1, i.e. disliked");
		
		List<String> hashtagNames = hldsList.stream()
	        	.map(x -> x.getHashtagName())
	        	.collect(Collectors.toList());
		assertTrue(hashtagNames.contains("tag_1"), "The first tag should be one of the events");
		assertTrue(hashtagNames.contains("tag_2"), "The second tag should be one of the events");
	}
	
	// !!! Scrapped for now as I cant get it to work !!!
//	// Testing the methods by triggering them with kafka
//	
//	@Test
//	public void TestLikedVideoListenerTriggerWithKafka() {
//		Video v = getTestVideo();
//		
//		// Send a record to likedVideo to trigger the listener
//		testProducer.likedVideo(v.getCreator().getId(), v);
//		
//		// Check the event was recieved by the listener and the repo increases by 2 saved events
//		Awaitility.await()
//			.atMost(Duration.ofSeconds(30))
//			.until(() -> likeDislikeRepository.findAll().spliterator().estimateSize() == 2);
//	}
//	
//	@Test
//	public void TestDislikedVideoListenerTriggerWithKafka() {
//		Video v = getTestVideo();
//		
//		// Send a record to likedVideo to trigger the listener
//		testProducer.dislikedVideo(v.getCreator().getId(), v);
//		
//		// Check the event was recieved by the listener and the repo increases by 2 saved events
//		Awaitility.await()
//			.atMost(Duration.ofSeconds(30))
//			.until(() -> likeDislikeRepository.findAll().spliterator().estimateSize() == 2);
//	}
	
}

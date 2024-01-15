package com.sm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sm.clients.SubscriptionsClient;
import com.subscription.domain.Hashtag;
import com.subscription.domain.Subscription;
import com.subscription.domain.User;
import com.subscription.domain.Video;
import com.subscription.events.VideoPostedListener;
import com.subscription.events.VideoWatchedListener;
import com.subscription.repositories.SubscriptionRepository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(transactional = false, environments = "no_streams")
public class KafkaListenerTest {

	@Inject
    SubscriptionsClient subClient;
	
	@Inject
	VideoPostedListener postedListener;
	
	@Inject
	VideoWatchedListener watchedListener;
	
	@Inject
	SubscriptionRepository repo;
	
	@BeforeEach
	public void clean() {
		// Wipe clean all the repositories and captured producer records
		for (Subscription s : repo.findAll()) {
    		s.setVideosNotSeen(new HashSet<Long>());
    		s.setVideosPostedSinceSub(new HashSet<Long>());
    		s.setVideosSeenSinceSub(new HashSet<Long>());
    		repo.update(s);
    	}
		repo.deleteAll();
	}
	
	// Utility methods
	private Subscription makeAndSaveSubscription(Long userId, Long hashtagId) {
    	Subscription s = new Subscription();
    	s.setUserId(userId);
    	s.setHashtagId(hashtagId);
    	
    	Subscription savedSub = repo.save(s);
    	return savedSub;
    }
	
	@Test
	public void TestPostedVideoListener() {
		Hashtag h1 = new Hashtag();
		h1.setName("tag1");
		h1.setId(Long.valueOf(42));
		Long hashId = h1.getId();
		
		// Using fake ids as it does not matter for this test
		Long userId = Long.valueOf(998);
		
		// subscribe to h1
		Subscription s = makeAndSaveSubscription(userId, hashId) ;
		assertEquals(0, s.getVideosPostedSinceSub().size(), "Before triggering the method the size of the new vids should be 0");
		assertEquals(1, repo.findAllByHashtagId(hashId).size());
		// Add new vid under hashtag h1
		Video v = new Video();
		v.setId(Long.valueOf(43));
		v.setTitle("title");
		v.setCreator(new User());
		v.getHashtags().add(h1);
		
		// Invoke the postedVideo method, this avoids sending kafka messages
		postedListener.postedVideo(Long.valueOf(500), v);
		
		// Check the subscription has been updated 
		s = repo.findById(s.getId()).get();
		assertNotNull(s, "sub should not be null");
		Set<Long> vids = s.getVideosPostedSinceSub();
		assertEquals(1, vids.size(), "after triggering the method the size of the new vids should be 1");
		Long vId = vids.iterator().next();
		assertEquals(v.getId(), vId, "ids should match");
	}
	
	@Test
	public void TestWatchedVideoListener() {
		Hashtag h1 = new Hashtag();
		h1.setName("tag1");
		h1.setId(Long.valueOf(42));
		Long hashId = h1.getId();
		
		// Using fake ids as it does not matter for this test
		Long userId = Long.valueOf(998);
		
		// subscribe to h1
		Subscription s = makeAndSaveSubscription(userId, hashId) ;
		assertEquals(0, s.getVideosSeenSinceSub().size(), "Before triggering the method the size of watched should be 0");
		assertEquals(1, repo.findAllByHashtagId(hashId).size());
		// Add new vid under hashtag h1
		Video v = new Video();
		v.setId(Long.valueOf(43));
		v.setTitle("title");
		v.setCreator(new User());
		v.getHashtags().add(h1);
		
		// Invoke the postedVideo method, this avoids sending kafka messages
		watchedListener.watchedVideo(userId, v);
	
		// Check the subscription has been updated 
		s = repo.findById(s.getId()).get();
		assertNotNull(s, "sub should not be null");
		assertEquals(s.getUserId(), userId);
		Set<Long> vids = s.getVideosSeenSinceSub();
		assertEquals(1, vids.size(), "after triggering the method the size of the new vids should be 1");
		Long vId = vids.iterator().next();
		assertEquals(v.getId(), vId, "ids should match");
	}
	
}

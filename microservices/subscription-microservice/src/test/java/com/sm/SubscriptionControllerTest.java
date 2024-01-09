package com.sm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.sm.clients.HashtagsClient;
import com.sm.clients.SubscriptionsClient;
import com.sm.clients.UsersClient;
import com.sm.clients.VideosClient;
import com.subscription.domain.Hashtag;
import com.subscription.domain.Subscription;
import com.subscription.domain.User;
import com.subscription.domain.Video;
import com.subscription.dto.VideoDTO;
import com.subscription.events.SubscriptionProducer;
import com.subscription.repositories.SubscriptionRepository;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(transactional = false, environments = "no_streams")
public class SubscriptionControllerTest {
	
	@Inject
    SubscriptionsClient subClient;
	
	@Inject
    VideosClient videoClient;
	
	@Inject
    UsersClient userClient;
	
	@Inject
    HashtagsClient hashtagClient;

    @Inject
    SubscriptionRepository subRepository;
    
    //	 Mocking producers
	private final Map<Long, Long> unsubscribedHashtags = new HashMap<>();
	private final Map<Long, Long> subscribedHashtags = new HashMap<>();
	
	private void deleteAllVideos() {
		Iterable<Video> vs = videoClient.list();
		for (Video v : vs) {
			videoClient.deleteVideo(v.getId());
		}
	}
	
	private void deleteAllUsers() {
		Iterable<User> us = userClient.list();
		for (User u : us) {
			userClient.deleteUser(u.getId());
		}
	}
	
	private void deleteAllHashtags() {
		Iterable<Hashtag> hs = hashtagClient.list();
		for (Hashtag h : hs) {
			hashtagClient.deleteHashtag(h.getId());
		}
	}
	
	private User createAndGetVideo(String name, Long creatorId, String hashtagString) {
		VideoDTO vdto = new VideoDTO();
		vdto.setTitle(name);
		vdto.setCreatorId(creatorId);
		vdto.setHashtagString(hashtagString);
		HttpResponse<Void> response = videoClient.add(vdto);
	}
	
    @BeforeEach
	public void clean() {
		// Wipe clean all the repositories
    	subRepository.deleteAll();
    	deleteAllVideos();
    	deleteAllUsers();
    	deleteAllHashtags();
	}
    
    @MockBean(SubscriptionProducer.class)
    SubscriptionProducer MockVideoProducer() {
		return new SubscriptionProducer() {
			
			@Topic("hashtag-unsubscribed")
			public void unsubscribedFromHashtag(@KafkaKey long userId, long hashtagId) {
				unsubscribedHashtags.put(userId, hashtagId);
			}
			
			@Topic("hashtag-subscribed")
			public void subscribedToHashtag(@KafkaKey long userId, long hashtagId) {
				subscribedHashtags.put(userId, hashtagId);
			}
		};
	}
    
    // Testing list
    
    @Test
    public void TestListNoSubs() {
    	Iterable<Subscription> subs = subClient.list();
    	assertEquals(0, subs.spliterator().getExactSizeIfKnown(), "There should be no subs");
    }
    
    @Test
    public void TestList() {
    	// These user id and hashtag id wont exist, but its fine for this test
    	Subscription s = new Subscription();
    	s.setUserId(Long.valueOf(999));
    	s.setHashtagId(Long.valueOf(998));
    	subRepository.save(s);
    	Iterable<Subscription> subs = subClient.list();
    	assertEquals(1, subs.spliterator().getExactSizeIfKnown(), "There should be one sub");
    	Subscription sub = subs.iterator().next();
    	assertEquals(999, sub.getUserId(), "user id should match what was saved");
    	assertEquals(998, sub.getHashtagId(), "hashtag id should match what was saved");
    	assertEquals(0, sub.getVideosNotSeen().spliterator().getExactSizeIfKnown(), "no videos should be since since sub");
    	assertEquals(0, sub.getVideosPostedSinceSub().spliterator().getExactSizeIfKnown(), "no videos should be since since sub");
    	assertEquals(0, sub.getVideosSeenSinceSub().spliterator().getExactSizeIfKnown(), "no videos should be since since sub");
    }
    
    // Testing subscribeTo
    
    @Nested
    class TestNestForSubscribeTo {
    	
    	Hashtag h;
    	User u;
    	
    	
        @Test
        public void TestSubscribeToUserNotFound() {
        	Subscription s = new Subscription();
        	s.setUserId(Long.valueOf(999));
        	s.setHashtagId(Long.valueOf(998));
        }
        
        @Test
        public void TestSubscribeToHashtagNotFound() {
        	
        }
    	
    	@Test
        public void TestSubscribeTo() {
        	
        }
        
    	@AfterAll
        public void cleanUp(){
    		// Remove hashtags
        	if (h != null) {
        		hashtagClient.deleteHashtag(h.getId());
        	}
        }
        
    }
    
    
    // Testing unsubscribeTo
    
    @Test
    public void TestUnsubscribeTo() {
    	
    }
    
    // Testing getTopTenVideos
    
    @Test
    public void testGetTopTenVideos() {
    	
    }
    
}
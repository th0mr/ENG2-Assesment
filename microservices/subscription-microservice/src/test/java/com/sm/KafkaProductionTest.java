package com.sm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sm.clients.SubscriptionsClient;
import com.sm.repositories.HashtagRepository;
import com.sm.repositories.UsersRepository;
import com.sm.repositories.VideosRepository;
import com.subscription.controllers.SubscriptionController;
import com.subscription.controllers.clients.HashtagsClient;
import com.subscription.controllers.clients.UsersClient;
import com.subscription.controllers.clients.VideosClient;
import com.subscription.domain.Hashtag;
import com.subscription.domain.Subscription;
import com.subscription.domain.User;
import com.subscription.domain.Video;
import com.subscription.repositories.SubscriptionRepository;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@Property(name = "spec.name", value = "KafkaProductionTest")
@MicronautTest(transactional = false, environments = "no_streams")
public class KafkaProductionTest {
    
    //	 Mocking producers
	private static final Map<Long, Long> subscribedTags = new HashMap<>();
	private static final Map<Long, Long> unsubscribedTags = new HashMap<>();
    
	@Inject
    SubscriptionController subscriptionController;
	
	@Inject
    SubscriptionsClient subClient;

    @Inject
    SubscriptionRepository subRepository;
    
    @Inject
    VideosRepository vidRepository;
    
    @Inject
    UsersRepository userRepository;
    
    @Inject
    HashtagRepository hashtagRepository;
	
    @BeforeEach
	public void clean() {
		// Wipe clean all the repositories
    	vidRepository.deleteAll();
    	userRepository.deleteAll();
    	hashtagRepository.deleteAll();
    	for (Subscription s : subRepository.findAll()) {
    		s.setVideosNotSeen(new HashSet<Long>());
    		s.setVideosPostedSinceSub(new HashSet<Long>());
    		s.setVideosSeenSinceSub(new HashSet<Long>());
    		subRepository.update(s);
    	}
    	subRepository.deleteAll();
    	subscribedTags.clear();
    	unsubscribedTags.clear();
	}

    
    @Inject
    @MockBean(VideosClient.class)
    VideosClient videoClient = MockVideosClient();
    
    @Inject
    @MockBean(UsersClient.class)
    UsersClient userClient = MockUsersClient();
    
    @Inject
    @MockBean(HashtagsClient.class)
    HashtagsClient hashtagClient = MockHashtagsClient();
    
    // Mocking videos client to avoid http requests to vm
    
    VideosClient MockVideosClient() {
		return new VideosClient() {
			
			@Override
			public Video getVideo(long id) {
				Optional<Video> v = vidRepository.findById(id);
				if (v.isEmpty()) {
					return null;
				}
				return v.get();
			}

			@Override
			public Iterable<User> getViewers(long id){
				Optional<Video> v = vidRepository.findById(id);
				if (v.isEmpty()) {
					return null;
				}
				return v.get().getViewers();
			}

		};
	}
    
    
    // Mocking user clients to avoid http requests to vm
    UsersClient MockUsersClient() {
		return new UsersClient() {
			
			@Override
			public User getUser(long id) {
				Optional<User> u = userRepository.findById(id);
				if (u.isEmpty()) {
					return null;
				}
				return u.get();
			}
			
			@Override
			public Iterable<Video> getUserWatchedVideos(long id){
				Optional<User> u = userRepository.findById(id);
				if (u.isEmpty()) {
					return null;
				}
				return u.get().getWatchedVideos();
			}

		};
	}
    
    // Mocking hashtags clients to avoid http requests to vm
    HashtagsClient MockHashtagsClient() {
		return new HashtagsClient() {
			
			@Override
			public Hashtag getHashtag(long id) {
				Optional<Hashtag> h = hashtagRepository.findById(id);
				if (h.isEmpty()) {
					return null;
				}
				return h.get();
			}
			
			@Override
			public Iterable<Video> getHashtagVideos(long id) {
				Optional<Hashtag> h = hashtagRepository.findById(id);
				if (h.isEmpty()) {
					return null;
				}
				return h.get().getVideos();
			}
		};
	}
    
    private Subscription makeAndSaveSubscription(Long userId, Long hashtagId) {
    	Subscription s = new Subscription();
    	s.setUserId(userId);
    	s.setHashtagId(hashtagId);
    	
    	Subscription savedSub = subRepository.save(s);
    	return savedSub;
    }
    
    private Hashtag makeAndSaveHashtag(String tagname) {
    	Hashtag h = new Hashtag();
    	h.setName(tagname);
    	Hashtag savedHashtag = hashtagRepository.save(h);
    	return savedHashtag;
    }
    
    private User makeAndSaveUser(String username) {
    	User u = new User();
    	u.setUsername(username);
    	User savedUser = userRepository.save(u);
    	return savedUser;
    }
    
	@Test
	public void addSubscriber() {
    	User u = makeAndSaveUser("test_user");
    	Hashtag h1 = makeAndSaveHashtag("tag1");
    	
    	final Long userId = u.getId();
    	HttpResponse<Void> response = subClient.subscribeTo(u.getId(), h1.getId());
		assertEquals(HttpStatus.CREATED, response.getStatus(), "subscribing should be okay");

		// Check the event went to Kafka and back
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.until(() -> subscribedTags.containsKey(userId));
	}
	
    @Test
	public void unsubscribeFromHashtag() {
    	User u = makeAndSaveUser("test_user");
		Hashtag h1 = makeAndSaveHashtag("tag1");
		makeAndSaveSubscription(u.getId(), h1.getId());

		final Long userId = u.getId();
		HttpResponse<Void> response = subClient.unsubscribeFrom(u.getId(), h1.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "unsubscribing should be okay");

		// Check the event went to Kafka and back
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.until(() -> unsubscribedTags.containsKey(userId));
	}
    
    
    @Requires(property = "spec.name", value = "KafkaProductionTest")
	@KafkaListener(groupId = "kafka-production-test")
	static class TestConsumer {
    	
		@Topic("hashtag-unsubscribed")
		void unsubscribedFromHashtag(@KafkaKey Long id, Long tagid) {
			unsubscribedTags.put(id, tagid);
		}
		
		@Topic("hashtag-subscribed")
		void subscribedToHashtag(@KafkaKey Long id, Long tagid) {
			subscribedTags.put(id, tagid);
		}

	}
}
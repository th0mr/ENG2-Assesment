package com.sm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

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
import com.subscription.domain.VideoViewsPair;
import com.subscription.events.SubscriptionProducer;
import com.subscription.repositories.SubscriptionRepository;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(transactional = false, environments = "no_streams")
public class SubscriptionControllerTest {
	
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
    
    //	 Mocking producers
	private final Map<Long, Long> unsubscribedHashtags = new HashMap<>();
	private final Map<Long, Long> subscribedHashtags = new HashMap<>();
	
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
    	unsubscribedHashtags.clear();
    	subscribedHashtags.clear();
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
//
//    @MockBean(SubscriptionController.class)
//    SubscriptionController mockSubController(){
//    	return new SubscriptionController() {
//        	VideosClient videoClient = MockVideosClient();
//        	UsersClient userClient = MockUsersClient();
//        	HashtagsClient hashtagClient = MockHashtagsClient();
//    	};
//    }
    
    
    // Utility methods
    
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
    
    private Video makeAndSaveVideo(String title,  User user) {
    	Video v = new Video();
    	v.setTitle(title);
    	v.setCreator(null);
    	Video savedVideo = vidRepository.save(v);
    	return savedVideo;
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
    	Subscription s = makeAndSaveSubscription(Long.valueOf(999),Long.valueOf(998));
    	Iterable<Subscription> subs = subClient.list();
    	assertEquals(1, subs.spliterator().getExactSizeIfKnown(), "There should be one sub");
    	Subscription sub = subs.iterator().next();
    	assertEquals(s.getUserId(), sub.getUserId(), "user id should match what was saved");
    	assertEquals(s.getHashtagId(), sub.getHashtagId(), "hashtag id should match what was saved");
    	assertEquals(0, sub.getVideosNotSeen().spliterator().getExactSizeIfKnown(), "no videos should be since since sub");
    	assertEquals(0, sub.getVideosPostedSinceSub().spliterator().getExactSizeIfKnown(), "no videos should be since since sub");
    	assertEquals(0, sub.getVideosSeenSinceSub().spliterator().getExactSizeIfKnown(), "no videos should be since since sub");
    }
    
    // Testing subscribeTo
    
    @Test
    public void TestSubscribeTo() {
    	User u = makeAndSaveUser("testUser");    	
    	Hashtag h = makeAndSaveHashtag("testTag");
    	Long userId = u.getId();
    	Long hashtagId = h.getId();    	
    	
    	HttpResponse<Void> response = subClient.subscribeTo(userId, hashtagId);
    	assertEquals(HttpStatus.CREATED, response.getStatus(), "The subscription should have been created succesfully");
    	assertEquals(1, subRepository.count(), "count should be 1 after subscribing");
    	
    	// Test a record has been sent to the subscribed_hashtag topic
    	assertTrue(subscribedHashtags.containsKey(u.getId()), "A record should have been sent to the mocked hashtag_subscribed topic");
    }
    
    @Test
    public void TestSubscribeToVidsFromHashtag() {
    	User u = makeAndSaveUser("testUser");    	
    	Hashtag h = makeAndSaveHashtag("testTag");
    	Video v = makeAndSaveVideo("video", u);
    	v.getHashtags().add(h);
    	vidRepository.update(v);
    	h.getVideos().add(v);
    	hashtagRepository.update(h);
    	Long userId = u.getId();
    	Long hashtagId = h.getId();    	
    	
    	HttpResponse<Void> response = subClient.subscribeTo(userId, hashtagId);
    	assertEquals(HttpStatus.CREATED, response.getStatus(), "The subscription should have been created succesfully");
    	assertEquals(1, subRepository.count(), "count should be 1 after subscribing");
    	assertEquals(1, subRepository.findAll().iterator().next().getVideosNotSeen().size(), "One vid from the hashtag should be unseen");
    
    	// Test a record has been sent to the subscribed_hashtag topic
    	assertTrue(subscribedHashtags.containsKey(u.getId()), "A record should have been sent to the mocked hashtag_subscribed topic");
    }    
    
    @Test
    public void TestSubscribeToVidsFromHashtagAlreadySeen() {
    	User u = makeAndSaveUser("testUser");    	
    	Hashtag h = makeAndSaveHashtag("testTag");
    	Video v = makeAndSaveVideo("video", u);
    	h.getVideos().add(v);
    	hashtagRepository.update(h);
    	u.getWatchedVideos().add(v);
    	userRepository.update(u);
    	Long userId = u.getId();
    	Long hashtagId = h.getId();    	
    	
    	HttpResponse<Void> response = subClient.subscribeTo(userId, hashtagId);
    	assertEquals(HttpStatus.CREATED, response.getStatus(), "The subscription should have been created succesfully");
    	assertEquals(1, subRepository.count(), "count should be 1 after subscribing");
    	assertEquals(0, subRepository.findAll().iterator().next().getVideosNotSeen().size(), "No vids from the hashtag should be unseen");
    
    	// Test a record has been sent to the subscribed_hashtag topic
    	assertTrue(subscribedHashtags.containsKey(u.getId()), "A record should have been sent to the mocked hashtag_subscribed topic");
    }    
    
    @Test
    public void TestSubscribeToUserNotFound() {
    	Hashtag h = makeAndSaveHashtag("testTag");
    	HttpResponse<Void> response = subClient.subscribeTo(999, h.getId());
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "The subscription shouldve have not been created as user does not exist");
    }
    
    @Test
    public void TestSubscribeToHashtagNotFound() {
    	User u = makeAndSaveUser("testUser");
    	HttpResponse<Void> response = subClient.subscribeTo(u.getId(), 999);
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "The subscription shouldve have not been created as user does not exist");
    }
    
    @Test
    public void TestUnsubscribeTo() {
    	// These user id and hashtag id wont exist, but its fine for this test
    	Subscription s = makeAndSaveSubscription(Long.valueOf(999),Long.valueOf(998));
    	assertEquals(1, subRepository.count(), "count should be one after subscribing");
    	HttpResponse<Void> response = subClient.unsubscribeFrom(s.getUserId(), s.getHashtagId());
    	assertEquals(HttpStatus.OK, response.getStatus(), "The unsubscription should be succesful");
    	assertEquals(0, subRepository.count(), "count should be zero after unsubscribing");
    
    	// Test a record has been sent to the unsubscribed_hashtag topic
    	assertTrue(unsubscribedHashtags.containsKey(Long.valueOf(999)), "A record should have been sent to the mocked hashtag_unsubscribed topic");
    }
    
    @Test
    public void TestUnsubscribeToSubNotFound() {
    	// These user id and hashtag id wont exist, but its fine for this test
    	HttpResponse<Void> response = subClient.unsubscribeFrom(999, 999);
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "The subscription should have been created succesfully");
    }
    
    // Testing getTopTenVideos
    
    @Test
    public void testGetTopTenVideosSubNotFound() {  
    	Iterable<VideoViewsPair> videos = subClient.getTopTenVideos(999, 998);
    	assertNull(videos, "No videos should be reccomended as sub does not exist");
    }
    
    @Test
    public void testGetTopTenVideosOneVidBefore() {
    	// Make a video from before the user subscribed
    	User u = makeAndSaveUser("testUser");
    	Hashtag h1 = makeAndSaveHashtag("tag1");
    	Video v = makeAndSaveVideo("firstVid", u);
    	
    	Subscription s = makeAndSaveSubscription(u.getId(),h1.getId());
    	s.getVideosNotSeen().add(v.getId());
    	subRepository.update(s);
    	Iterable<VideoViewsPair> videos = subClient.getTopTenVideos(s.getUserId(), s.getHashtagId());
    	assertNotNull(videos, "videos should not be null");
    	assertEquals(1, videos.spliterator().getExactSizeIfKnown(), "The subscriber has not seen the video posted before the sub, and should be recommended it");
    	VideoViewsPair pair = videos.iterator().next();
    	assertEquals(v.getTitle(), pair.getVideo().getTitle(), "Titles should match");
    	assertEquals(v.getViewers().size(), pair.getViews(), "views should match");
    }
    
    @Test
    public void testGetTopTenVideosOneVidBeforeThenAnotherAdded() {
    	// Make a video from before the user subscribed
    	User u = makeAndSaveUser("testUser");
    	Hashtag h1 = makeAndSaveHashtag("tag1");
    	Video v = makeAndSaveVideo("firstVid", u);
    	
    	Subscription s = makeAndSaveSubscription(u.getId(),h1.getId());
    	s.getVideosNotSeen().add(v.getId());
    	subRepository.update(s);
    	Iterable<VideoViewsPair> videos = subClient.getTopTenVideos(s.getUserId(), s.getHashtagId());
    	assertNotNull(videos, "videos should not be null");
    	assertEquals(1, videos.spliterator().getExactSizeIfKnown(), "The subscriber has not seen the video posted before the sub, and should be recommended it");
    	VideoViewsPair pair = videos.iterator().next();
    	assertEquals(v.getTitle(), pair.getVideo().getTitle(), "Titles should match");
    	assertEquals(v.getViewers().size(), pair.getViews(), "views should match");
    	
    	Video v2 = makeAndSaveVideo("secondVid", u);
    	s.getVideosNotSeen().add(v2.getId());
    	subRepository.update(s);
    	
    	videos = subClient.getTopTenVideos(s.getUserId(), s.getHashtagId());
    	assertNotNull(videos, "videos should not be null");
    	assertEquals(2, videos.spliterator().getExactSizeIfKnown(), "The subscriber has not seen the video posted before the sub, and should be recommended it");

    }

    @Test
    public void testGetTopTenVideosThenWatchVideo() {
    	// Make a video from before the user subscribed
    	User u = makeAndSaveUser("testUser");
    	Hashtag h1 = makeAndSaveHashtag("tag1");
    	Video v = makeAndSaveVideo("firstVid", u);
    	
    	Subscription s = makeAndSaveSubscription(u.getId(),h1.getId());
    	s.getVideosNotSeen().add(v.getId());
    	subRepository.update(s);
    	Iterable<VideoViewsPair> videos = subClient.getTopTenVideos(s.getUserId(), s.getHashtagId());
    	assertNotNull(videos, "videos should not be null");
    	assertEquals(1, videos.spliterator().getExactSizeIfKnown(), "The subscriber has not seen the video posted before the sub, and should be recommended it");
    	VideoViewsPair pair = videos.iterator().next();
    	assertEquals(v.getTitle(), pair.getVideo().getTitle(), "Titles should match");
    	assertEquals(v.getViewers().size(), pair.getViews(), "views should match");
    	
    	s.getVideosSeenSinceSub().add(v.getId());
    	subRepository.update(s);
    	videos = subClient.getTopTenVideos(s.getUserId(), s.getHashtagId());
    	assertEquals(0, videos.spliterator().getExactSizeIfKnown(), "The subscriber has now seen the video so no recommending it");
    }
    
    @Test
    public void testGetTopTenVideosNoVideos() {
    	// Make a video from before the user subscribed
    	User u = makeAndSaveUser("testUser");
    	Hashtag h1 = makeAndSaveHashtag("tag1");
    	Subscription s = makeAndSaveSubscription(u.getId(),h1.getId());
    	
    	Iterable<VideoViewsPair> videos = subClient.getTopTenVideos(s.getUserId(), s.getHashtagId());
    	assertEquals(0, videos.spliterator().getExactSizeIfKnown(), "The subscriber has not seen the video posted before the sub, and should be recommended it");
    }
    
}
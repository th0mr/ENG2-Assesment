package com.video;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.video.clients.VideosClient;
import com.video.domain.User;
import com.video.domain.Video;
import com.video.dto.VideoDTO;
import com.video.repositories.HashtagRepository;
import com.video.repositories.UsersRepository;
import com.video.repositories.VideosRepository;
import com.video.events.VideoProducer;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@Property(name = "spec.name", value = "KafkaProductionTest")
@MicronautTest(transactional = false, environments = "no_streams")
public class KafkaProductionTest {

    @Inject
    VideosClient videoClient;
    
    @Inject
    VideosRepository videosRepository;
    
    @Inject
    UsersRepository usersRepository;
    
    @Inject
    HashtagRepository hashtagsRepository;
    
    //	 Mocking producers
	private static final Map<Long, Video> watchedVideos = new HashMap<>();
	private static final Map<Long, Video> likedVideos = new HashMap<>();
	private static final Map<Long, Video> dislikedVideos = new HashMap<>();
	private static final Map<Long, Video> postedVideos = new HashMap<>();
    
    @BeforeEach
	public void clean() {
		// Wipe clean all the repositories and captured producer records
		videosRepository.deleteAll();
		usersRepository.deleteAll();
		hashtagsRepository.deleteAll();
		watchedVideos.clear();
		likedVideos.clear();
		dislikedVideos.clear();
		postedVideos.clear();
	}
    
    // Test utils
    public User createAndSaveUser(String username) {
		User u = new User();
		u.setUsername(username);
		u = usersRepository.save(u);
		return u;
	}
	
	public User createAndSaveUser() {
		return createAndSaveUser("test_user");
	}
	
	public Video createAndSaveVideo(String title, User u) {
		Video v = new Video();
		v.setTitle(title);
		v.setCreator(u);
		v = videosRepository.save(v);
		return v;
	}
	
	public Video createAndSaveVideo() {
		return createAndSaveVideo("test_video", createAndSaveUser());
	}
    
	@Test
	public void addVideoPoster() {
    	User u = createAndSaveUser("poster_test_user");
    	
    	VideoDTO dto = new VideoDTO();
    	dto.setTitle("poster_test_video");
    	dto.setCreatorId(u.getId());
    	
    	final Long userId = u.getId();
        HttpResponse<Void> response = videoClient.add(dto);
		assertEquals(HttpStatus.CREATED, response.getStatus(), "user posting the video should be successful");

		// Check the event went to Kafka and back
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.until(() -> postedVideos.containsKey(userId));
	}
	
    @Test
	public void addVideoViewer() {
    	User u = createAndSaveUser("viewer_test_user");
		Video v = createAndSaveVideo("viewer_test_video", u);

		final Long userId = u.getId();
		HttpResponse<String> response = videoClient.addViewer(v.getId(), u.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "Adding viewer to the video should be successful");

		// Check the event went to Kafka and back
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.until(() -> watchedVideos.containsKey(userId));
	}
    
    @Test
	public void addVideoLiker() {
    	User u = createAndSaveUser("liker_test_user");
		Video v = createAndSaveVideo("liker_test_video", u);

		final Long userId = u.getId();
		HttpResponse<String> response = videoClient.addLiker(v.getId(), u.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "Adding liker to the video should be successful");

		// Check the event went to Kafka and back
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.until(() -> likedVideos.containsKey(userId));
	}
    
    @Test
	public void addVideoDisliker() {
    	User u = createAndSaveUser("disliker_test_user");
		Video v = createAndSaveVideo("disliker_test_video", u);

		final Long userId = u.getId();
		HttpResponse<String> response = videoClient.addDisliker(v.getId(), u.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "Adding disliker to the video should be successful");

		// Check the event went to Kafka and back
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.until(() -> dislikedVideos.containsKey(userId));
	}
    
    
    @Requires(property = "spec.name", value = "KafkaProductionTest")
	@KafkaListener(groupId = "kafka-production-test")
	static class TestConsumer {
    	
		@Topic(VideoProducer.TOPIC_WATCHED)
		void watchedVideo(@KafkaKey Long id, Video book) {
			watchedVideos.put(id, book);
		}
		
		@Topic(VideoProducer.TOPIC_POSTED)
		void postedVideo(@KafkaKey Long id, Video book) {
			postedVideos.put(id, book);
		}
		
		@Topic(VideoProducer.TOPIC_LIKED)
		void likedVideo(@KafkaKey Long id, Video book) {
			likedVideos.put(id, book);
		}
		
		@Topic(VideoProducer.TOPIC_DISLIKED)
		void dislikedVideo(@KafkaKey Long id, Video book) {
			dislikedVideos.put(id, book);
		}
	}
}

package com.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.video.clients.VideosClient;
import com.video.domain.Hashtag;
import com.video.domain.User;
import com.video.domain.Video;
import com.video.dto.VideoDTO;
import com.video.events.VideoProducer;
import com.video.repositories.HashtagRepository;
import com.video.repositories.UsersRepository;
import com.video.repositories.VideosRepository;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(transactional = false, environments = "no_streams")
public class VideosControllerTest {

    @Inject
    VideosClient videoClient;
    
    @Inject
    VideosRepository videosRepository;
    
    @Inject
    UsersRepository usersRepository;
    
    @Inject
    HashtagRepository hashtagsRepository;
 
    //	 Mocking producers
	private final Map<Long, Video> watchedVideos = new HashMap<>();
	private final Map<Long, Video> likedVideos = new HashMap<>();
	private final Map<Long, Video> dislikedVideos = new HashMap<>();
	private final Map<Long, Video> postedVideos = new HashMap<>();

	// Mock the whole producer class, replacing the methods with ones that capture the records into
	// the above Maps so we can read them in tests.
	@MockBean(VideoProducer.class)
	VideoProducer watchedVideo() {
		return new VideoProducer() {
			
			@Override
			public void watchedVideo(@KafkaKey Long userId, Video v) {
				watchedVideos.put(userId, v);
			}
			
			@Override
			public void likedVideo(@KafkaKey Long userId, Video v) {
				likedVideos.put(userId, v);
			}
			
			@Override
			public void dislikedVideo(@KafkaKey Long userId, Video v) {
				dislikedVideos.put(userId, v);
			}
			
			@Override
			public void postedVideo(@KafkaKey Long userId, Video v) {
				postedVideos.put(userId, v);
			}
		};
	}

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
 	public void noVideos() {
		Iterable<Video> iterVideo = videoClient.list();
		assertFalse(iterVideo.iterator().hasNext(), "Service should not list any videos initially");
	}

	// Testing addVideo
    @Test
    public void testAddVideo() {
    	User u = createAndSaveUser();
    	
    	VideoDTO dto = new VideoDTO();
    	dto.setTitle("test_video");
    	dto.setCreatorId(u.getId());
    	dto.setHashtagString("tag1,tag2");
    	
        HttpResponse<Void> response = videoClient.add(dto);
        assertEquals(HttpStatus.CREATED, response.getStatus(), "Video should have been added succesfully");
        
        List<Video> videos = iterableToList(videoClient.list());
		assertEquals(1, videos.size());
		assertEquals("test_video", videos.get(0).getTitle());
		assertEquals(u.getId(), videos.get(0).getCreator().getId());
		System.out.println(videos.get(0).getHashtags());
		assertEquals(2, videos.get(0).getHashtags().size());
		
		// Test a record has been sent to the posted_video topic
		assertTrue(postedVideos.containsKey(u.getId()), "A record should have been sent to the mocked posted_video topic");
    }    
    
    // Testing getVideo
    @Test
    public void testGetVideo() {
    	Video v = createAndSaveVideo();
    	Video video = videoClient.getVideo(v.getId());
    	assertEquals(v.getId(), video.getId(), "Id should match the same one we saved");
    	assertEquals(v.getTitle(), video.getTitle(), "Title should match the same one we saved");
    }
    
    @Test
    public void testGetVideoVideoNotFound() {
    	// Id of 999 should not exist, so we request it
    	Video video = videoClient.getVideo(999);
    	assertNull(video, "video does not exist, so it should be null");
    }
    
    // Testing deleteVideo
    @Test
    public void testDeleteVideo() {
    	Video v = createAndSaveVideo();
    	HttpResponse<Void> response = videoClient.deleteVideo(v.getId());
    	assertEquals(HttpStatus.OK, response.getStatus(), "video exists and should have been deleted successfully");
    	assertEquals(0, videosRepository.findAll().spliterator().estimateSize(), "The repository should contain no videos");
    }
    
    @Test
    public void testDeleteVideoVideoNotFound() {
    	// Id of 999 should not exist, so we attempt request it
    	HttpResponse<Void> response = videoClient.deleteVideo(999);
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "video does not exist, so it should be null");
    }
    
    // Testing updateVideo
    @Test
    public void testUpdateVideos() {
    	User u = createAndSaveUser();
    	// Set-up a fully populated video
    	Video v = new Video();
		v.setTitle("test_video");
		v.setCreator(u);
		Set<Hashtag> hashtags = new HashSet<>();
		Hashtag h1 = new Hashtag();
		h1.setName("tag_1");
		Hashtag h2 = new Hashtag();
		h2.setName("tag_2");
		v.setHashtags(hashtags);
		v = videosRepository.save(v);
		
		User newUser = createAndSaveUser("new_user");
		String newTitle = "new_title";
		String newHashtags = "new_tag1,new_tag2,new_tag3";
		List<String> SeperatedNewHashtags = new ArrayList<String>(Arrays.asList(newHashtags.split(" , ")));

		// Create a DTO to replace all three attributes
		VideoDTO dto = new VideoDTO();
		dto.setCreatorId(newUser.getId());
		dto.setTitle(newTitle);
		dto.setHashtagString(newHashtags);
		
		HttpResponse<Void> response = videoClient.updateVideo(v.getId(), dto);
    	assertEquals(HttpStatus.OK, response.getStatus(), "Video should update successfully");
    	Video updated_v = videosRepository.findById(v.getId()).get();
    	assertEquals(newTitle, updated_v.getTitle(), "Title should match what we updated it to");
    	assertEquals(newUser.getId(), updated_v.getCreator().getId(), "Creator id should match what we updated it to");
    	assertEquals(3, updated_v.getHashtags().size(), "There should be three hashtags as we removed the previous ones and replaced them with 3 new ones");
    	for (Hashtag h : updated_v.getHashtags()) {
    		assertTrue(SeperatedNewHashtags.contains(h.getName()), h.getName() + " should not be in the updated hashtags");
    	}
    }
    
    @Test
    public void testUpdateVideosVideoNotFound() {
    	// Create a DTO to replace title
		VideoDTO dto = new VideoDTO();
		dto.setTitle("new_title");
		
    	// Id of 999 should not exist, so we attempt request it
    	HttpResponse<Void> response = videoClient.updateVideo(999, dto);
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "video does not exist, so update video should not be able to find it");
    }
    
    @Test
    public void testUpdateVideosUserNotFound() {
    	Video v = createAndSaveVideo();
    	// Create a DTO to replace title + creatorId
		VideoDTO dto = new VideoDTO();
		dto.setTitle("new_title");
		// Id of 999 should not exist, so we attempt swap to the user
		dto.setCreatorId(999);
    	HttpResponse<Void> response = videoClient.updateVideo(v.getId(), dto);
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "user does not exist, so update video should not be able to find it");
    	// Confirm name and userId has not changed
    	Video fetchedVideo = videosRepository.findById(v.getId()).get();
    	assertEquals(v.getTitle(), fetchedVideo.getTitle(), "Title should still match the pre-update attempt state");
    	assertEquals(v.getCreator().getId(), fetchedVideo.getCreator().getId(), "Title should still match the pre-update attempt state");
    }
    
    // Testing getHashtags
	@Test
	public void testGetHashtags() {
		Video v = createAndSaveVideo();
		Hashtag h1 = new Hashtag();
		h1.setName("tag1");
		hashtagsRepository.save(h1);
		Hashtag h2 = new Hashtag();
		h2.setName("tag2");
		hashtagsRepository.save(h2);
		
		// Add hashtags
		HttpResponse<String> response = videoClient.addHashtag((long)v.getId(), (long)h1.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "hashtag should be added sucessfully");
		response = videoClient.addHashtag((long)v.getId(), (long)h2.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "hashtag should be added sucessfully");
		
		//Check hashtags were added
		List<Hashtag> hashtags = iterableToList(videoClient.getHashtags((long)v.getId()));
		assertEquals(2, hashtags.size(), "two hashtags should be added to the video");
	}
	
	@Test
	public void testGetHashtagsVideoNotFound() {
		// Get hashtags for video with id 999, which should not exist
		Iterable<Hashtag> hashtags = videoClient.getHashtags(999);
		// Should return null if the video does not exist
		assertNull(hashtags);
	}
	
	// Testing addHashtags
	@Test
	public void testAddHashtag() {
		Video v = createAndSaveVideo();
		Hashtag h1 = new Hashtag();
		h1.setName("tag1");
		hashtagsRepository.save(h1);
		
		// Add hashtags
		HttpResponse<String> response = videoClient.addHashtag((long)v.getId(), (long)h1.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "hashtag should be added sucessfully");
		
		v = videosRepository.findById(v.getId()).get();
		Set<Hashtag> hashtags = v.getHashtags();
		assertEquals(1, hashtags.size(), "one hashtag should be added to the video");
	}
	
	@Test
	public void testAddHashtagVideoNotFound() {
		Hashtag h1 = new Hashtag();
		h1.setName("tag1");
		hashtagsRepository.save(h1);
		
		// Add hashtags to id 999, which is a video that should not exist
		HttpResponse<String> response = videoClient.addHashtag(999, (long)h1.getId());
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "hashtag should be added un-sucessfully");
	}
		
	// Testing addHashtags
	@Test	
	public void testAddHashtagHashtagNotFound() {
		Video v = createAndSaveVideo();
		
		// Add hashtag with id 999 that should not exist
		HttpResponse<String> response = videoClient.addHashtag((long)v.getId(), 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "hashtag should be added un-sucessfully");
		
		v = videosRepository.findById(v.getId()).get();
		Set<Hashtag> hashtags = v.getHashtags();
		assertEquals(0, hashtags.size(), "the video should still have 0 hashtags");
	}
	
	// Viewer Related Code
	@Test
	public void testNoVideoViewers() {
		Video v = createAndSaveVideo();

		List<User> viewers = iterableToList(videoClient.getViewers(v.getId()));
		assertEquals(0, viewers.size(), "Video should not have any viewers initially");
	}
	
	@Test
	public void testAddVideoViewer() {
		Video v = createAndSaveVideo();

		String viewerName = "first_viewer";
		User u = createAndSaveUser(viewerName);

		HttpResponse<String> response = videoClient.addViewer(v.getId(), u.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "Adding viewer to the video should be successful");

		// Check the producer was called by the addition
		assertTrue(watchedVideos.containsKey(u.getId()));

		v = videosRepository.findById(v.getId()).get();
		assertEquals(1, v.getViewers().size(), "Video should now have 1 viewer");
		assertEquals(viewerName, v.getViewers().iterator().next().getUsername());
	}
	
	@Test
	public void testAddVideoViewerVideoNotFound() {
		// Attempt to add viewer with ID 999, on video 998, both of which should not exist
		HttpResponse<String> response = videoClient.addViewer(998, 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "adding viewer from a non-existent video should be un-successful");
	}
	
	@Test
	public void testAddVideoViewerUserNotFound() {
		Video v = createAndSaveVideo();
		
		assertTrue(v.getViewers().size()==0, "video should have no viewers to begin with");

		// Attempt to add viewer with ID 999, which should not exist
		HttpResponse<String> response = videoClient.addViewer(v.getId(), 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Removing viewer from the video should be un-successful");

		v = videosRepository.findById(v.getId()).get();
		assertTrue(v.getViewers().size()==0, "video should still have no viewers");
	}
	
	@Test
	public void testDeleteVideoViewer() {
		Video v = createAndSaveVideo();

		String viewerName = "first_viewer";
		User u = createAndSaveUser(viewerName);
		
		v.getViewers().add(u);
		videosRepository.update(v);

		HttpResponse<String> response = videoClient.removeViewer(v.getId(), u.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "Removing viewer to the video should be successful");

		v = videosRepository.findById(v.getId()).get();
		assertTrue(v.getViewers().isEmpty(), "video should have no viewer anymore");
	}
	
	@Test
	public void testDeleteVideoViewerVideoNotFound() {
		// Attempt to delete liker with ID 999, on video 998, both of which should not exist
		HttpResponse<String> response = videoClient.removeViewer(998, 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Removing viewer from a non-existent video should be un-successful");
	}
	
	@Test
	public void testDeleteVideoViewerUserNotFound() {
		Video v = createAndSaveVideo();

		String viewerName = "first_viewer";
		User u = createAndSaveUser(viewerName);
		
		v.getViewers().add(u);
		videosRepository.update(v);

		// Attempt to delete viewer with ID 999, which should not exist
		HttpResponse<String> response = videoClient.removeViewer(v.getId(), 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Removing viewer from the video should be un-successful");

		v = videosRepository.findById(v.getId()).get();
		assertTrue(v.getViewers().size()==1, "video should still have 1 viewer");
	}

	@Test
	public void testGetViewers() {
		User u1 = createAndSaveUser("user1");
		User u2 = createAndSaveUser("user2");
		Video v = new Video();
		v.setTitle("test_video");
		v.setCreator(u1);
		v.getViewers().add(u1);
		v.getViewers().add(u2);
		v = videosRepository.save(v);
		
		Iterable<User> viewers = videoClient.getViewers(v.getId());
		assertEquals(2, viewers.spliterator().getExactSizeIfKnown());
	}
		
	// Liker Related Code
	@Test
	public void testNoVideoLiker() {
		Video v = createAndSaveVideo();

		List<User> likers = iterableToList(videoClient.getLikers(v.getId()));
		assertEquals(0, likers.size(), "Video should not have any likers initially");
	}
	
	@Test
	public void testAddVideoLiker() {
		Video v = createAndSaveVideo();

		String likerName = "first_liker";
		User u = createAndSaveUser(likerName);

		HttpResponse<String> response = videoClient.addLiker(v.getId(), u.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "Adding liker to the video should be successful");

		// Check the producer was called by the addition
		assertTrue(likedVideos.containsKey(u.getId()));

		v = videosRepository.findById(v.getId()).get();
		assertEquals(1, v.getLikers().size(), "Video should now have 1 liker");
		assertEquals(likerName, v.getLikers().iterator().next().getUsername());
	}
	
	@Test
	public void testAddVideoLikerVideoNotFound() {
		// Attempt to add liker with ID 999, on video 998, both of which should not exist
		HttpResponse<String> response = videoClient.addLiker(998, 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "adding liker from a non-existent video should be un-successful");
	}
	
	@Test
	public void testAddVideoLikerUserNotFound() {
		Video v = createAndSaveVideo();
		
		assertTrue(v.getLikers().size()==0, "video have no likers to begin with");

		// Attempt to add liker with ID 999, which should not exist
		HttpResponse<String> response = videoClient.addLiker(v.getId(), 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Removing liker from the video should be un-successful");

		v = videosRepository.findById(v.getId()).get();
		assertTrue(v.getLikers().size()==0, "video should still have no likers");
	}
		
	@Test
	public void testDeleteVideoLiker() {
		Video v = createAndSaveVideo();

		String likerName = "first_liker";
		User u = createAndSaveUser(likerName);
		
		v.getLikers().add(u);
		videosRepository.update(v);

		HttpResponse<String> response = videoClient.removeLiker(v.getId(), u.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "Removing liker from the video should be successful");

		v = videosRepository.findById(v.getId()).get();
		assertTrue(v.getLikers().isEmpty(), "video should have no likers anymore");
	}
	
	@Test
	public void testDeleteVideoLikerVideoNotFound() {
		// Attempt to delete liker with ID 999, on video 998, both of which should not exist
		HttpResponse<String> response = videoClient.removeLiker(998, 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Removing liker from a non-existent video should be un-successful");
	}
	
	@Test
	public void testDeleteVideoLikerUserNotFound() {
		Video v = createAndSaveVideo();

		String likerName = "first_liker";
		User u = createAndSaveUser(likerName);
		
		v.getLikers().add(u);
		videosRepository.update(v);

		// Attempt to delete liker with ID 999, which should not exist
		HttpResponse<String> response = videoClient.removeLiker(v.getId(), 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Removing liker from the video should be un-successful");

		v = videosRepository.findById(v.getId()).get();
		assertTrue(v.getLikers().size()==1, "video should still have 1 liker");
	}

	@Test
	public void testGetLikers() {
		User u1 = createAndSaveUser("user1");
		User u2 = createAndSaveUser("user2");
		Video v = new Video();
		v.setTitle("test_video");
		v.setCreator(u1);
		v.getLikers().add(u1);
		v.getLikers().add(u2);
		v = videosRepository.save(v);
		
		Iterable<User> likers = videoClient.getLikers(v.getId());
		assertEquals(2, likers.spliterator().getExactSizeIfKnown());
	}
	
	// Disliker Related Code
	@Test
	public void testNoVideoDisliker() {
		Video v = createAndSaveVideo();

		List<User> dislikers = iterableToList(videoClient.getDislikers(v.getId()));
		assertEquals(0, dislikers.size(), "Video should not have any dislikers initially");
	}
	
	@Test
	public void testAddVideoDisliker() {
		Video v = createAndSaveVideo();

		String dislikerName = "first_disliker";
		User u = createAndSaveUser(dislikerName);

		HttpResponse<String> response = videoClient.addDisliker(v.getId(), u.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "Adding disliker to the video should be successful");

		// Check the producer was called by the addition
		assertTrue(dislikedVideos.containsKey(u.getId()));

		v = videosRepository.findById(v.getId()).get();
		assertEquals(1, v.getDislikers().size(), "Video should now have 1 disliker");
		assertEquals(dislikerName, v.getDislikers().iterator().next().getUsername());
	}
	
	@Test
	public void testAddVideoDislikerVideoNotFound() {
		// Attempt to add viewer with ID 999, on video 998, both of which should not exist
		HttpResponse<String> response = videoClient.addDisliker(998, 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "adding disliker from a non-existent video should be un-successful");
	}
	
	@Test
	public void testAddVideoDisikerUserNotFound() {
		Video v = createAndSaveVideo();
		
		assertTrue(v.getDislikers().size()==0, "video should have no dislikers to begin with");

		// Attempt to add viewer with ID 999, which should not exist
		HttpResponse<String> response = videoClient.addDisliker(v.getId(), 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Removing viewer from the video should be un-successful");

		v = videosRepository.findById(v.getId()).get();
		assertTrue(v.getDislikers().size()==0, "video should still have no dislikers");
	}
	
	@Test
	public void testDeleteVideoDisliker() {
		Video v = createAndSaveVideo();

		String dislikerName = "first_disliker";
		User u = createAndSaveUser(dislikerName);
		
		v.getDislikers().add(u);
		videosRepository.update(v);

		HttpResponse<String> response = videoClient.removeDisliker(v.getId(), u.getId());
		assertEquals(HttpStatus.OK, response.getStatus(), "Removing disliker from the video should be successful");

		v = videosRepository.findById(v.getId()).get();
		assertTrue(v.getDislikers().isEmpty(), "video should have no dislikers anymore");
	}
	
	@Test
	public void testDeleteVideoDislikerUserNotFound() {
		Video v = createAndSaveVideo();

		String dislikerName = "first_disliker";
		User u = createAndSaveUser(dislikerName);
		
		v.getDislikers().add(u);
		videosRepository.update(v);

		// Attempt to delete disliker with ID 999, which should not exist
		HttpResponse<String> response = videoClient.removeDisliker(v.getId(), 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Removing disliker from the video should be un-successful");

		v = videosRepository.findById(v.getId()).get();
		assertTrue(v.getDislikers().size()==1, "video should still have 1 disliker");
	}

	@Test
	public void testDeleteVideoDislikerVideoNotFound() {
		// Attempt to delete disliker with ID 999, on video 998, both of which should not exist
		HttpResponse<String> response = videoClient.removeDisliker(998, 999);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Removing disliker from a non-existent video should be un-successful");
	}
	
	@Test
	public void testGetDislikers() {
		User u1 = createAndSaveUser("user1");
		User u2 = createAndSaveUser("user2");
		Video v = new Video();
		v.setTitle("test_video");
		v.setCreator(u1);
		v.getDislikers().add(u1);
		v.getDislikers().add(u2);
		v = videosRepository.save(v);
		
		Iterable<User> dislikers = videoClient.getDislikers(v.getId());
		assertEquals(2, dislikers.spliterator().getExactSizeIfKnown());
	}
	
	// Utility methods
	private <T> List<T> iterableToList(Iterable<T> iterable) {
		List<T> l = new ArrayList<>();
		iterable.forEach(l::add);
		return l;
	}
}

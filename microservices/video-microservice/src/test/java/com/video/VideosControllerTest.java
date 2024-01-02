package com.video;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.video.clients.UsersClient;
import com.video.clients.VideosClient;
import com.video.domain.Hashtag;
import com.video.domain.User;
import com.video.domain.Video;
import com.video.dto.UserDTO;
import com.video.dto.VideoDTO;
import com.video.events.VideoProducer;
import com.video.repositories.HashtagRepository;
import com.video.repositories.UsersRepository;
import com.video.repositories.VideosRepository;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@MicronautTest(transactional = false, environments = "no_streams")
public class VideosControllerTest {

    @Inject
    VideosClient videoClient;
    
    @Inject
    UsersClient userClient;

    @Inject
    VideosRepository videosRepository;
    
    @Inject
    UsersRepository usersRepository;
    
    @Inject
    HashtagRepository hashtagsRepository;
 
	// Mocking producers
	// private final Map<Long, Book> Books = new HashMap<>();

//	@MockBean(VideoProducer.class)
//	VideoProducer testProducer() {
//		return (key, value) -> { readBooks.put(key,  value); };
//	}
    
	@BeforeEach
	public void clean() {
		videosRepository.deleteAll();
		usersRepository.deleteAll();
		hashtagsRepository.deleteAll();
	}
	
	@Test
	public void noVideos() {
		Iterable<Video> iterVideo = videoClient.list();
		assertFalse(iterVideo.iterator().hasNext(), "Service should not list any videos initially");
	}

    @Test
    public void testAddVideo() {
    	User u = new User();
		u.setUsername("add_video_test_user");
		User createdUser = usersRepository.save(u);
    	// Created user should have ID of 1 as long as clean() has sucessfully wiped the user repo
    	VideoDTO dto = new VideoDTO();
    	dto.setTitle("test_video");
    	dto.setCreatorId(createdUser.getId());
    	dto.setHashtagString("tag1,tag2");
        HttpResponse<Void> response = videoClient.add(dto);
        assertEquals(HttpStatus.CREATED, response.getStatus(), "Video should have been added succesfully");
        
        List<Video> videos = iterableToList(videoClient.list());
		assertEquals(1, videos.size());
		assertEquals("test_video", videos.get(0).getTitle());
		assertEquals(createdUser.getId(), videos.get(0).getCreator().getId());
		System.out.println(videos.get(0).getHashtags());
		assertEquals(2, videos.get(0).getHashtags().size());
    }
    
	@Test
	public void testGetVideo() {
		User u = new User();
		u.setUsername("get_video_test_user");
		usersRepository.save(u);
		
		Video v = new Video();
		v.setTitle("get_video_test_video");
		v.setCreator(u);
		
		videosRepository.save(v);
		VideoDTO videoDTO = videoClient.getVideo(v.getId());
		System.out.println("id=" + videoDTO.getCreatorId());
		assertEquals(v.getTitle(), videoDTO.getTitle(), "Title should match");
		assertNotNull(v.getCreator().getId(), "creatorId should not be null if set");
	}

    @Test
    public void testUpdateVideo() {
        User u = new User();
		u.setUsername("test_user");
		usersRepository.save(u);
		
		Video v = new Video();
		v.setTitle("test_video");
		v.setCreator(u);
		videosRepository.save(v);

		String newTitle = "new_title";
		VideoDTO updateObject = new VideoDTO();
		updateObject.setTitle(newTitle);
		HttpResponse<Void> response = videoClient.updateVideo(v.getId(), updateObject);
		assertEquals(HttpStatus.OK, response.getStatus());
		
		v = videosRepository.findById(v.getId()).get();
		assertEquals(newTitle, v.getTitle());
    }
    
	@Test
	public void testGetMissingVideo2() {
		VideoDTO response = videoClient.getVideo(0);
		assertNull(response, "A missing video should produce return null");
	}
    
	@Test
	public void testGetMissingVideo() {
		VideoDTO response = videoClient.getVideo(0);
		assertNull(response, "A missing video should produce return null");
	}
//
//    @Test
//    void testDeleteVideo() {
//        // Add a video to delete
//        VideoDTO videoDTO = new VideoDTO("Test Video", 1L, "tag1,tag2");
//        HttpRequest<VideoDTO> addRequest = HttpRequest.POST("/videos", videoDTO);
//        HttpResponse<Void> addResponse = client.toBlocking().exchange(addRequest, Void.class);
//
//        // Verify the response status code for adding a video
//        assertEquals(HttpResponse.created("/videos/1"), addResponse);
//
//        // Construct a request to delete the video
//        HttpRequest<Void> deleteRequest = HttpRequest.DELETE("/videos/1");
//        HttpResponse<Void> deleteResponse = client.toBlocking().exchange(deleteRequest, Void.class);
//
//        // Verify the response status code for deleting the video
//        assertEquals(HttpResponse.ok(), deleteResponse);
//
//        // Verify the video is no longer in the database
//        assertEquals(0, videosRepository.count());
//    }
    

	private <T> List<T> iterableToList(Iterable<T> iterable) {
		List<T> l = new ArrayList<>();
		iterable.forEach(l::add);
		return l;
	}
}

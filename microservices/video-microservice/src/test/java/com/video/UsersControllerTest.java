package com.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.video.clients.UsersClient;
import com.video.domain.User;
import com.video.domain.Video;
import com.video.dto.UserDTO;
import com.video.repositories.HashtagRepository;
import com.video.repositories.UsersRepository;
import com.video.repositories.VideosRepository;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(transactional = false, environments = "no_streams")
public class UsersControllerTest {
	
	@Inject
    UsersClient userClient;

    @Inject
    VideosRepository videosRepository;
    
    @Inject
    UsersRepository usersRepository;
    
    @Inject
    HashtagRepository hashtagsRepository;

    @BeforeEach
	public void clean() {
		// Wipe clean all the repositories
		videosRepository.deleteAll();
		usersRepository.deleteAll();
		hashtagsRepository.deleteAll();
	}
    
    // Test utility methods
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
	
	private <T> List<T> iterableToList(Iterable<T> iterable) {
		List<T> l = new ArrayList<>();
		iterable.forEach(l::add);
		return l;
	}
	
    // testing list
    @Test
    public void testList() {
    	createAndSaveUser("u1");
    	createAndSaveUser("u2");
    	
    	List<User> users = iterableToList(userClient.list());
        assertEquals(2, users.size(), "There should be two users in the repository");
    }
    
    // testing addUser
    @Test
    public void testAddUser() {
    	UserDTO dto = new UserDTO();
    	dto.setUsername("new_user");
    	HttpResponse<Void> response = userClient.add(dto);
    	assertEquals(HttpStatus.CREATED, response.getStatus(), "user should be created sucessfully");
    	assertEquals(1, usersRepository.findAll().spliterator().estimateSize(), "there should be one user created");
    }
    
    // testing getUser
    @Test
    public void testGetUser() {
    	User u = createAndSaveUser();
    	User user = userClient.getUser(u.getId());
    	assertNotNull(user, "user should not be null, as we just created it");
        assertEquals(u.getUsername(), user.getUsername(), "The name of the fetched user should match the one we created");
    }
    
    @Test
    public void testGetUserNotFound() {
    	// id 999 should not exist, so we request that
    	User user = userClient.getUser(999);
    	assertNull(user, "We expect the response to be null, as the user does not exist");
    }
    
    // Testing updateUser
    @Test
    public void testUpdateUser() {
    	User u = createAndSaveUser();
    	UserDTO dto = new UserDTO();
    	String newUsername = "new_username";
    	dto.setUsername(newUsername);
    	
    	HttpResponse<Void> response = userClient.updateUser(u.getId(), dto);
    	assertEquals(HttpStatus.OK, response.getStatus(), "update should be successfull");
    	u = usersRepository.findById(u.getId()).get();
    	assertEquals(newUsername, u.getUsername(), "the new username should match the one we updated it to");
    }
    
    @Test
    public void testUpdateUserUserNotFound() {
    	UserDTO dto = new UserDTO();
    	String newUsername = "new_username";
    	dto.setUsername(newUsername);
    	
    	// user id 999 should not exist, so we request it
    	HttpResponse<Void> response = userClient.updateUser(999, dto);
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "update should be un-successfull");
    }
    
    // Testing deleteUser
    @Test
    public void testDeleteUser() {
    	User u = createAndSaveUser();
    	HttpResponse<Void> response = userClient.deleteUser(u.getId());
    	assertEquals(HttpStatus.OK, response.getStatus(), "deletion should be successfull");
    	List<User> users = iterableToList(usersRepository.findAll());
    	assertEquals(0, users.size(), "No users should be present after the deletion");
    }
    
    @Test
    public void testDeleteUserUserNotFound() {
    	HttpResponse<Void> response = userClient.deleteUser(999);
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "deletion should be un-successfull");
    }
	
    // Testing getVideos
    @Test
    public void testGetVideos() {
    	User u = createAndSaveUser("test_user");
    	// Make user u create a video
    	String videoName = "test_video";
    	Video v = createAndSaveVideo(videoName, u);
    	List<Video> videos = iterableToList(userClient.getUserVideos(u.getId()));
    	assertEquals(1, videos.size(), "user should have one registered created video");
    	assertEquals(v.getTitle(), videos.get(0).getTitle(), "the video should be called " + videoName);
    }
    
    @Test
    public void testGetVideosUserNotFound() {
    	// user id 999 should not exist, so we request their videos
    	Iterable<Video> videos = userClient.getUserVideos(999);
    	assertNull(videos, "null should be returned as the user does not exist");
    }
    
    // Testing getLikedVideos
    @Test
    public void testGetLikedVideos() {
    	User u = createAndSaveUser("test_user");
    	// User u creates a video
    	String videoName = "test_video";
    	Video v = createAndSaveVideo(videoName, u);
    	// Add u as a liker to video
    	v.getLikers().add(u);
    	videosRepository.update(v);
    	List<Video> videos = iterableToList(userClient.getUserLikedVideos(u.getId()));
    	assertEquals(1, videos.size(), "user should have one liked video");
    	assertEquals(videoName, videos.get(0).getTitle(), "the liked video should be called " + videoName);
    }
    
    @Test
    public void testGetLikedVideosUserNotFound() {
    	// user id 999 should not exist, so we request their videos
    	Iterable<Video> videos = userClient.getUserLikedVideos(999);
    	assertNull(videos, "null should be returned as the user does not exist");
    }
    
    @Test
    public void testGetDislikedVideos() {
    	User u = createAndSaveUser("test_user");
    	// User u creates a video
    	String videoName = "test_video";
    	Video v = createAndSaveVideo(videoName, u);
    	// Add u as a disliker to video
    	v.getDislikers().add(u);
    	videosRepository.update(v);
    	List<Video> videos = iterableToList(userClient.getUserDislikedVideos(u.getId()));
    	assertEquals(1, videos.size(), "user should have one disliked video");
    	assertEquals(videoName, videos.get(0).getTitle(), "the disliked video should be called " + videoName);
    }
    
    @Test
    public void testGetDislikedVideosUserNotFound() {
    	// user id 999 should not exist, so we request their videos
    	Iterable<Video> videos = userClient.getUserDislikedVideos(999);
    	assertNull(videos,"null should be returned as the user does not exist");
    }
    
    @Test
    public void testGetWatchedVideos() {
    	User u = createAndSaveUser("test_user");
    	// User u creates a video
    	String videoName = "test_video";
    	Video v = createAndSaveVideo(videoName, u);
    	// Add u as a watcher of a video
    	v.getViewers().add(u);
    	videosRepository.update(v);
    	List<Video> videos = iterableToList(userClient.getUserWatchedVideos(u.getId()));
    	assertEquals(1, videos.size(), "user should have one watched video");
    	assertEquals(videoName, videos.get(0).getTitle(), "the watched video should be called " + videoName);
    }
    
    @Test
    public void testGetWatchedVideosUserNotFound() {
    	// user id 999 should not exist, so we request their videos
    	Iterable<Video> videos = userClient.getUserWatchedVideos(999);
    	assertNull(videos,"null should be returned as the user does not exist");
    }
    
}

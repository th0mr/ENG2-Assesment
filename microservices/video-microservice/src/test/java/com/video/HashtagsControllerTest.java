package com.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.video.clients.HashtagsClient;
import com.video.domain.Hashtag;
import com.video.domain.User;
import com.video.domain.Video;
import com.video.dto.HashtagDTO;
import com.video.repositories.HashtagRepository;
import com.video.repositories.UsersRepository;
import com.video.repositories.VideosRepository;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(transactional = false, environments = "no_streams")
public class HashtagsControllerTest {
	
	@Inject
    HashtagsClient hashtagClient;

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
	
	public Hashtag createAndSaveHashtag(String tagName) {
		Hashtag h = new Hashtag();
		h.setName(tagName);
		h = hashtagsRepository.save(h);
		return h;
	}
	
	public Hashtag createAndSaveHashtag() {
		return createAndSaveHashtag("tag_1");
	}
	
	private <T> List<T> iterableToList(Iterable<T> iterable) {
		List<T> l = new ArrayList<>();
		iterable.forEach(l::add);
		return l;
	}
	
	// Testing list
	
    @Test
    public void testList() {
    	createAndSaveHashtag("tag_1");
    	createAndSaveHashtag("tag_2");
    	
    	List<Hashtag> hashtags = iterableToList(hashtagClient.list());
        assertEquals(2, hashtags.size(), "There should be two hashtags in the repository");
    }
	
	// Testing add
    @Test
    public void testAdd() {
    	HashtagDTO dto = new HashtagDTO();
    	dto.setName("tag_1");
    	assertEquals(0, hashtagsRepository.findAll().spliterator().estimateSize(), "there should exist no hashtags before we add one");
    	HttpResponse<Void> response = hashtagClient.add(dto);
    	assertEquals(HttpStatus.CREATED, response.getStatus(), "hashtag should be added successfully");
    	assertEquals(1, hashtagsRepository.findAll().spliterator().estimateSize(), "there should now exist one hashtag");
    }
    
    @Test
    public void testAddHashtagAlreadyExists() {
    	// Create a tag called tag_1
    	createAndSaveHashtag("tag_1");
    	// Create a dto for a tag also called tag_1
    	HashtagDTO dto = new HashtagDTO();
    	dto.setName("tag_1");
    	assertEquals(1, hashtagsRepository.findAll().spliterator().estimateSize(), "there should exist 1 hashtag before we attempt to add a second one");
    	HttpResponse<Void> response = hashtagClient.add(dto);
    	assertEquals(HttpStatus.OK, response.getStatus(), "response should be ok, as we already have that hashtag exist");
    	assertEquals(1, hashtagsRepository.findAll().spliterator().estimateSize(), "there still be 1 hashtag in the repository, as we should have failed to create one");
    }
    
    // testing getHashtag
    @Test
    public void testGetHashtag() {
    	Hashtag h = createAndSaveHashtag();
    	Hashtag hashtag = hashtagClient.getHashtag(h.getId());
    	assertNotNull(hashtag, "hashtag should not be null, as we just created it");
        assertEquals(h.getName(), hashtag.getName(), "The name of the fetched hashtag should match the one we created");
    }
    
    @Test
    public void testGetHashtagHashtagNotFound() {
    	// id 999 should not exist, so we request that
    	Hashtag hashtag = hashtagClient.getHashtag(999);
    	assertNull(hashtag, "We expect the response to be null, as the hashtag does not exist");
    }
    
    // testing deleteHashtag
    @Test
    public void testDeleteHashtag() {
    	Hashtag h = createAndSaveHashtag();
    	assertEquals(1, hashtagsRepository.findAll().spliterator().estimateSize(), "there should exist 1 hashtag before we attempt to delete one");
    	HttpResponse<Void> response = hashtagClient.deleteHashtag(h.getId());
    	assertEquals(HttpStatus.OK, response.getStatus(), "deletion should be successfull");
    	assertEquals(0, hashtagsRepository.findAll().spliterator().estimateSize(), "there should exist no hashtags after we attempt to delete one");
    }
    
    @Test
    public void testDeleteHashtagHashtagNotFound() {
    	// id 999 should not exist, so we request that
    	HttpResponse<Void> response = hashtagClient.deleteHashtag(999);
    	assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "deletion should be unsuccessfull");
    }
    
    // Testing getVideos
    @Test
    public void testNoVideosExistFirst() {
    	Hashtag h = createAndSaveHashtag();
    	Iterable<Video> videos = hashtagClient.getHashtagVideos(h.getId());
    	assertNotNull(videos, "videos should have no contents, but should not be null");
    	assertEquals(0, videos.spliterator().estimateSize(), "There should be no videos upon creation of a hashtag");
    }
    
    @Test
    public void testGetVideos() {
    	Hashtag h = createAndSaveHashtag();
    	Video v = createAndSaveVideo();
    	// add hashtag to video and update repo
    	v.getHashtags().add(h);
    	videosRepository.update(v);
    	Iterable<Video> videos = hashtagClient.getHashtagVideos(h.getId());
    	assertNotNull(videos, "videos should have contents and should not be null");
    	assertEquals(1, videos.spliterator().estimateSize(), "There should be 1 videos linked to that hashtag");
    	assertEquals(v.getTitle(), iterableToList(videos).get(0).getTitle(), "Title should match the one we linked");
    }
    
    @Test
    public void testGetVideosHashtagNotFound() {
    	// id 999 should not exist, so we request that
    	Iterable<Video> videos = hashtagClient.getHashtagVideos(999);
    	assertNull(videos, "hashtag doesnt exist, the result should be null");
    }
}

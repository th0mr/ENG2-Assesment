package com.thm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.thm.clients.TrendingHashtagsClient;
import com.trendinghashtag.domain.HashLikesPair;
import com.trendinghashtag.domain.HashtagLikedDislikedEvent;
import com.trendinghashtag.repositories.HashtagLikedDislikedEventRepository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(transactional = false, environments = "no_streams")
public class TrendingHashtagControllerTest {
	
	@Inject
    TrendingHashtagsClient trendingHashtagsClient;
	
	@Inject
	HashtagLikedDislikedEventRepository likeDislikeRepository;
	
	@BeforeEach
	public void clean() {
		// Wipe clean all the repositories and captured producer records
		likeDislikeRepository.deleteAll();
	}
	
	public HashtagLikedDislikedEvent createAndSaveLikeDislikedEvent(String tagname, LocalDateTime timestamp, int value) {
		HashtagLikedDislikedEvent hlde = new HashtagLikedDislikedEvent();
		hlde.setHashtagName(tagname);
		hlde.setTimestamp(timestamp);
		hlde.setValue(value);
		likeDislikeRepository.save(hlde);
		return hlde;
	}
	
	public List<HashtagLikedDislikedEvent> createManyLikeDislikeEventsAtATimestamp(int amount, String tagname, LocalDateTime timestamp, int value){
		List<HashtagLikedDislikedEvent> hlds = new ArrayList<HashtagLikedDislikedEvent>();
		for (int i = 0; i < amount; i++) {
			hlds.add(createAndSaveLikeDislikedEvent(tagname, timestamp, value));
		}
		return hlds;
	}
	
	public HashtagLikedDislikedEvent createAndSaveLikeEvent(String tagname, LocalDateTime timestamp) {
		return createAndSaveLikeDislikedEvent(tagname, timestamp, 1);
	}
	
	public HashtagLikedDislikedEvent createAndSaveDislikeEvent(String tagname, LocalDateTime timestamp) {
		return createAndSaveLikeDislikedEvent(tagname, timestamp, -1);
	}
	
	public List<HashtagLikedDislikedEvent> createManyLikeEventsAtATimestamp(int amount, String tagname, LocalDateTime timestamp){
		return createManyLikeDislikeEventsAtATimestamp(amount, tagname, timestamp, 1);
	}
	
	public List<HashtagLikedDislikedEvent> createManyDislikeEventsAtATimestamp(int amount, String tagname, LocalDateTime timestamp){
		return createManyLikeDislikeEventsAtATimestamp(amount, tagname, timestamp, -1);
	}
	
	// Testing list
	
	@Test
	public void testListWithOneLikedTag() {
		LocalDateTime currentTime = LocalDateTime.now();
		String tagname = "tag1";
		HashtagLikedDislikedEvent hld = createAndSaveLikeEvent(tagname, currentTime);
		
		List<HashLikesPair> hashLikePairs = trendingHashtagsClient.list();
		assertEquals(1, hashLikePairs.size(), "Only one hashLikePair should be in the result");
		assertEquals(tagname, hashLikePairs.get(0).getHashname(), "The top tag should be called" + tagname);
		assertEquals(1, hashLikePairs.get(0).getLikeCount(), "The tag should have one like");
	}
	
	@Test
	public void testListWithTwoLikedTags() {
		LocalDateTime currentTime = LocalDateTime.now();
		
		// Create two like events for tag1, and one for tag2
		String tagname1 = "tag1";
		createManyLikeEventsAtATimestamp(2, tagname1, currentTime);
		String tagname2 = "tag2";
		createAndSaveLikeEvent(tagname2, currentTime);
		
		List<HashLikesPair> hashLikePairs = trendingHashtagsClient.list();
		assertEquals(2, hashLikePairs.size(), "Only two hashLikePair should be in the result");
		// Confirm tag1 is higher ranked, i.e. first in the list
		assertEquals(tagname1, hashLikePairs.get(0).getHashname(), "The top tag should be called" + tagname1);
		assertEquals(2, hashLikePairs.get(0).getLikeCount(), "The tag should have two likes");
		// Confirm tag2 is the second highest, i.e. second in the list
		assertEquals(tagname2, hashLikePairs.get(1).getHashname(), "The second highest tag should be called" + tagname2);
		assertEquals(1, hashLikePairs.get(1).getLikeCount(), "The tag should have one like");
	}
	
	@Test
	public void testListWithOneLikedOneDislikedTag() {
		LocalDateTime currentTime = LocalDateTime.now();
		
		// Create a like event for tag1, and a dislike for tag2
		String tagname1 = "tag1";
		createAndSaveLikeEvent(tagname1, currentTime);
		String tagname2 = "tag2";
		createAndSaveDislikeEvent(tagname2, currentTime);
		
		List<HashLikesPair> hashLikePairs = trendingHashtagsClient.list();
		assertEquals(2, hashLikePairs.size(), "Only two hashLikePair should be in the result");
		// Confirm tag1 is higher ranked
		assertEquals(tagname1, hashLikePairs.get(0).getHashname(), "The top tag should be called" + tagname1);
		assertEquals(1, hashLikePairs.get(0).getLikeCount(), "The tag should have two likes");
		// Confirm tag2 is the second highest with a negative like count, but is still trending
		assertEquals(tagname2, hashLikePairs.get(1).getHashname(), "The second highest tag should be called" + tagname2);
		assertEquals(-1, hashLikePairs.get(1).getLikeCount(), "The tag should have one dislike");
	}
	
	@Test
	public void testListWithTwoTagsAndManyEvents() {
		LocalDateTime currentTime = LocalDateTime.now();
		
		// Create two like events for tag1, 99 like events for tag2 and 98 dislike events for tag2
		String tagname1 = "tag1";
		createManyLikeEventsAtATimestamp(2, tagname1, currentTime);
		String tagname2 = "tag2";
		createManyLikeEventsAtATimestamp(99, tagname2, currentTime);
		createManyDislikeEventsAtATimestamp(98, tagname2, currentTime);
		
		List<HashLikesPair> hashLikePairs = trendingHashtagsClient.list();
		assertEquals(2, hashLikePairs.size(), "Only two hashLikePair should be in the result");
		// Confirm tag1 is higher ranked, i.e. first in the list
		assertEquals(tagname1, hashLikePairs.get(0).getHashname(), "The top tag should be called" + tagname1);
		assertEquals(2, hashLikePairs.get(0).getLikeCount(), "The tag should have two likes");
		// Confirm tag2 is the second highest, i.e. second in the list, 99 - 98 = 1
		assertEquals(tagname2, hashLikePairs.get(1).getHashname(), "The second highest tag should be called" + tagname2);
		assertEquals(1, hashLikePairs.get(1).getLikeCount(), "The tag should have one like");
	}
	
	@Test
	public void testListDoesNotCountOldRecords() {
		LocalDateTime currentTime = LocalDateTime.now();
		// trending-hashtags should not include records made in the past hour
		// make a recent event with one like event and one with a like event over an hour ago
		String tagname1 = "tag1";
		createAndSaveLikeEvent(tagname1, currentTime);
		String tagname2 = "tag2";
		createManyLikeEventsAtATimestamp(2, tagname2, currentTime.minusHours(2));
		
		List<HashLikesPair> hashLikePairs = trendingHashtagsClient.list();
		// If the service is not working, tagname2 will be higher ranked, with two likes
		assertEquals(1, hashLikePairs.size(), "Only one hashLikePair should be in the result");
		// Confirm tag1 is higher ranked, i.e. first in the list
		assertEquals(tagname1, hashLikePairs.get(0).getHashname(), "The top tag should be called" + tagname1);
		assertEquals(1, hashLikePairs.get(0).getLikeCount(), "The tag should have one like");
	}
	
}

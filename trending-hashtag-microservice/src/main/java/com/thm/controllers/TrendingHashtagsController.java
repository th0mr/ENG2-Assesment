package com.thm.controllers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thm.domain.HashLikesPair;
import com.thm.domain.Hashtag;
import com.thm.domain.HashtagLikedDislikedEvent;
import com.thm.repositories.HashtagLikedDislikedEventRepository;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

@Controller("/trending-hashtags")
public class TrendingHashtagsController {

    @Inject
    private HashtagLikedDislikedEventRepository repo;

	@Get("/")
	public List<HashLikesPair> list() {
		
		// Create a map to keep count of the like/dislike total
		Map<String, Integer> map = new HashMap<>();
		
		LocalDateTime anHourAgo = LocalDateTime.now().minusHours(1);
		List<HashtagLikedDislikedEvent> events = repo.findEventsAfterTime(anHourAgo);
		System.out.println("All events in the last hour:");
		
		// Iterate through each event and assemble a map of hashtagName to like total
		for (HashtagLikedDislikedEvent event : events) {
			String hashtagName = event.getHashtagName();
			int eventValue = event.getValue();
			// While were iterating, lets print it out for debugging
			System.out.println(event);
			// This will add +1 for likes and -1 for dislikes
			map.put(hashtagName, map.getOrDefault(hashtagName, 0) + eventValue);
		}
		
		List<Map.Entry<String, Integer>> sortedEntries = map.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

		List<HashLikesPair> output = new LinkedList<>();
		
		for (Entry<String, Integer> e : sortedEntries) {
			HashLikesPair hlp = new HashLikesPair();
			hlp.setHashname(e.getKey());
			hlp.setLikeCount(e.getValue());
			output.add(hlp);
		}
		
		return output;
	}
}

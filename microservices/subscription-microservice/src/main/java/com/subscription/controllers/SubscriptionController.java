// protected region packageDefinition on begin
package com.subscription.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import com.subscription.controllers.clients.HashtagsClient;
import com.subscription.controllers.clients.UsersClient;
import com.subscription.controllers.clients.VideosClient;
import com.subscription.domain.Hashtag;
import com.subscription.domain.Subscription;
import com.subscription.domain.User;
import com.subscription.domain.Video;
import com.subscription.domain.VideoViewsPair;
import com.subscription.repositories.SubscriptionRepository;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

// protected region packageDefinition end

@Controller("/subscription")
public class SubscriptionController {

	// protected region classVariables on begin
	
	@Inject
    private SubscriptionRepository repo;
	
	// Clients for communicating with video microservice
	@Inject
	private VideosClient videoClient;
	
	@Inject
	private UsersClient userClient;
	
	@Inject
	private HashtagsClient hashtagClient;
	
	// protected region classVariables end
	
	// Utility method
	private <T> Set<T> iterableToSet(Iterable<T> iterable) {
		Set<T> l = new HashSet<>();
		iterable.forEach(l::add);
		return l;
	}
	
	@Get("/")
	public Iterable<Subscription> list() {
		return repo.findAll();
	}
	
	// Creates a subscription object
	@Post("/{userId}/{hashtagId}")
	public HttpResponse<Void> subscribeTo(long userId, long hashtagId) {
		// Confirm user exists
		User u = userClient.getUser(userId);
		if (u == null) {
			return HttpResponse.notFound();
		}
		// Confirm hashtag exists
		Hashtag h = hashtagClient.getHashtag(hashtagId);
		if (h == null) {
			return HttpResponse.notFound();
		}
		
		// protected region methodContents on begin
		Subscription sub = new Subscription();
		sub.setUserId(userId);
		sub.setHashtagId(hashtagId);
		
		// Populate videos not seen with all the videos, minus those already seen by the user
		// From after this point, the microservice will maintain and update this list
		// from vm events.
		Set<Long> vidsUserHasSeen = new HashSet<Long>();
		for (Video v : userClient.getUserWatchedVideos(userId)) {
			vidsUserHasSeen.add(v.getId());
		}
		Set<Long> vidsFromHashtag = new HashSet<Long>();
		for (Video v : hashtagClient.getHashtagVideos(hashtagId)) {
			vidsFromHashtag.add(v.getId());
		}
		vidsFromHashtag.removeAll(vidsUserHasSeen);
		
		sub.setVideosNotSeen(vidsFromHashtag);
		
		URI uri = URI.create("/subscription/" + userId + "/" + hashtagId);
		Subscription createdSub = repo.save(sub);
		return HttpResponse.created(uri);
		// protected region methodContents end
	}
	
	@Transactional
	@Delete("{userId}/{hashtagId}")
	public HttpResponse<Void> unsubscribeFrom(long userId, long hashtagId) {
		// protected region methodContents on begin
		Optional<Subscription> sub = repo.findByUserIdAndHashtagId(userId, hashtagId);
		if (sub.isEmpty()) {
			return HttpResponse.notFound();
		}

		repo.delete(sub.get());
		return HttpResponse.ok();
		// protected region methodContents end
	}
	
	private int getVideoViews(Video v) {
		Iterable<User> users = videoClient.getViewers(v.getId());
		return (int) users.spliterator().getExactSizeIfKnown();
	}
	
	// Returns an ordered list of VideoViewsPairs that 
	@Get("{userId}/{hashtagId}/recommendations")
	public List<VideoViewsPair> getTopTenVideos(long userId, long hashtagId) {
		// protected region methodContents on begin
		
		// Check the user has a subscription for that hashtag
		Optional<Subscription> sub = repo.findByUserIdAndHashtagId(userId, hashtagId);
		if (sub.isEmpty()) {
			return null;
		}
		
		Subscription subscription = sub.get();
		
		// Get all the videos from that hashtag to populate the list
		Set<Long> videoIds = subscription.getVideosNotSeen();
		videoIds.addAll(subscription.getVideosPostedSinceSub());
		videoIds.removeAll(subscription.getVideosSeenSinceSub());
		
		// We now have a set of videos not seen by that user since their subscription
		// Now to determine the order in which they are recommended
		// First assembling a set of Video -> views pairs
		Set<VideoViewsPair> videoViewPairs = new HashSet<VideoViewsPair>();
		for (Long vidId : videoIds) {
			
			Video v = videoClient.getVideo(vidId);
			if (v == null) {
				// The video must have been deleted since it was added
				continue;
			}
			
			int views = getVideoViews(v);
			VideoViewsPair pair = new VideoViewsPair();
			pair.setVideo(v);
			pair.setViews(views);
			videoViewPairs.add(pair);
		}
		
		// Now to order that set into an ordered list of VideoViewsPairs
		// decending in like count
		List<VideoViewsPair> sortedList = new ArrayList<>(videoViewPairs);
		Collections.sort(sortedList, Comparator.comparingInt(VideoViewsPair::getViews).reversed());
		
		return sortedList;
		// protected region methodContents end
	}

}
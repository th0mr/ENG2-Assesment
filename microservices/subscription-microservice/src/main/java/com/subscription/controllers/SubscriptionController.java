// protected region packageDefinition on begin
package com.subscription.controllers;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;

import com.subscription.domain.Subscription;
import com.subscription.domain.Video;
import com.subscription.repositories.SubscriptionRepository;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

// protected region packageDefinition end

@Controller("/subscription")
public class SubscriptionController {

	// protected region classVariables on begin
	
	@Inject
    private SubscriptionRepository repo;
	
	// protected region classVariables end
	
	// Creates a subscription object
	@Post("/")
	public HttpResponse<Void> subscribeTo(long userId, long hashtagId) {
		// protected region methodContents on begin
		Subscription sub = new Subscription();
		sub.setUserId(userId);
		sub.setHashtagId(hashtagId);
		
		URI uri = URI.create("/subscription/" + userId + "/" + hashtagId);
		Subscription createdSub = repo.save(sub);
		return HttpResponse.created(uri);
		// protected region methodContents end
	}
	@Transactional
	@Delete("{userId}/{hashtagId}")
	public HttpResponse<Void> unsubscribeFrom(long userId, long hashtagId) {
		// protected region methodContents on begin
		Optional<Subscription> sub = repo.findByUserId(userId);
		if (video.isEmpty()) {
			return HttpResponse.notFound();
		}

		repo.delete(video.get());
		return HttpResponse.ok();
		// protected region methodContents end
	}

	@Post("{userId}/{hashtagId}/recommendations")
	public Iterable<Video> getTopTenVideos(long userId, long hashtagId) {
		// protected region methodContents on begin
		System.out.println("Method is not implemented");
		// protected region methodContents end
	}

}
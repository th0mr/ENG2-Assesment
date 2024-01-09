package com.sm.clients;
import java.util.List;

import com.subscription.domain.Subscription;
import com.subscription.domain.VideoViewsPair;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("/subscription")
public interface SubscriptionsClient {

	@Get("/")
	Iterable<Subscription> list();
	
	@Post("/{userId}/{hashtagId}")
	public HttpResponse<Void> subscribeTo(long userId, long hashtagId);
	
	@Delete("{userId}/{hashtagId}")
	public HttpResponse<Void> unsubscribeFrom(long userId, long hashtagId);
	
	@Post("{userId}/{hashtagId}/recommendations")
	public List<VideoViewsPair> getTopTenVideos(long userId, long hashtagId);
}
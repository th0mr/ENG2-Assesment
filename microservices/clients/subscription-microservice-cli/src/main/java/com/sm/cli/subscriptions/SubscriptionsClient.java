package com.sm.cli.subscriptions;

import java.util.List;

import com.sm.cli.domain.VideoViewsPair;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("${subscription.url:`http://localhost:8082/subscription`}")
public interface SubscriptionsClient {

	@Post("/{userId}/{hashtagId}")
	public HttpResponse<Void> subscribeTo(long userId, long hashtagId);
	
	@Delete("{userId}/{hashtagId}")
	public HttpResponse<Void> unsubscribeFrom(long userId, long hashtagId);
	
	@Post("{userId}/{hashtagId}/recommendations")
	public List<VideoViewsPair> getTopTenVideos(long userId, long hashtagId);
}

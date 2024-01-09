package com.sm.clients;


import com.subscription.domain.Hashtag;
import com.subscription.domain.User;
import com.subscription.domain.Video;
import com.subscription.dto.VideoDTO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.annotation.Client;

@Client("/videos")
public interface VideosClient {

	@Get("/")
	Iterable<Video> list();

	@Post("/")
	HttpResponse<Void> add(@Body VideoDTO videoDetails);

	@Get("/{id}")
	Video getVideo(long id);

	@Put("/{id}")
	HttpResponse<Void> updateVideo(long id, @Body VideoDTO videoDetails);

	@Delete("/{id}")
	HttpResponse<Void> deleteVideo(long id);

	// Creator
	
	@Get("/{id}/creator")
	public Iterable<User> getCreator(long id);

	@Put("/{videoId}/creator/{userId}")
	public HttpResponse<String> setCreator(long videoId, long userId);
	
	// Hashtags
	
	@Put("/{videoId}/hashtags/{hashtagId}")
	public HttpResponse<String> addHashtag(long videoId, long hashtagId);
	
	@Get("/{id}/hashtags")
	public Iterable<Hashtag> getHashtags(long id);
	
	// Viewers
	
	@Get("/{id}/viewers")
	public Iterable<User> getViewers(long id);

	@Put("/{videoId}/viewers/{userId}")
	public HttpResponse<String> addViewer(long videoId, long userId);

	@Delete("/{videoId}/viewers/{userId}")
	public HttpResponse<String> removeViewer(long videoId, long userId);
	
	// Liking
	
	@Get("/{id}/likers")
	public Iterable<User> getLikers(long id);
	
	@Put("/{videoId}/likers/{userId}")
	public HttpResponse<String> addLiker(long videoId, long userId);

	@Delete("/{videoId}/likers/{userId}")
	public HttpResponse<String> removeLiker(long videoId, long userId);
	
	// Disliking
	
	@Get("/{id}/dislikers")
	public Iterable<User> getDislikers(long id);
	
	@Put("/{videoId}/dislikers/{userId}")
	public HttpResponse<String> addDisliker(long videoId, long userId);

	@Delete("/{videoId}/dislikers/{userId}")
	public HttpResponse<String> removeDisliker(long videoId, long userId);
}

package com.video.cli.videos;


import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.annotation.Client;
import com.video.cli.domain.Video;
import com.video.cli.domain.User;
import com.video.cli.dto.VideoDTO;

@Client("${videos.url:`http://localhost:8080/videos`}")
public interface VideosClient {

	@Get("/")
	Iterable<Video> list();

	@Post("/")
	HttpResponse<Void> add(@Body VideoDTO videoDetails);

	@Get("/{id}")
	VideoDTO getVideo(long id);

	@Put("/{id}")
	HttpResponse<Void> updateVideo(long id, @Body VideoDTO videoDetails);

	@Delete("/{id}")
	HttpResponse<Void> deleteVideo(long id);

	// Creator
	
	@Get("/{id}/creator")
	public Iterable<User> getCreator(long id);

	@Put("/{videoId}/creator/{userId}")
	public HttpResponse<String> setCreator(long videoId, long userId);
	
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

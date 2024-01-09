package com.sm.clients;


import com.subscription.domain.User;
import com.subscription.domain.Video;
import com.subscription.dto.UserDTO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.annotation.Client;

@Client("/users")
public interface UsersClient {

	@Get("/")
	Iterable<User> list();

	@Post("/")
	HttpResponse<Void> add(@Body UserDTO userDetails);

	@Get("/{id}")
	User getUser(long id);

	@Put("/{id}")
	HttpResponse<Void> updateUser(long id, @Body UserDTO userDetails);

	@Delete("/{id}")
	HttpResponse<Void> deleteUser(long id);
	
	@Get("/{id}/videos")
	Iterable<Video> getUserVideos(long id);
	
	@Get("/{id}/liked")
	Iterable<Video> getUserLikedVideos(long id);
	
	@Get("/{id}/disliked")
	Iterable<Video> getUserDislikedVideos(long id);

	@Get("/{id}/watched")
	Iterable<Video> getUserWatchedVideos(long id);
	
	
	
}

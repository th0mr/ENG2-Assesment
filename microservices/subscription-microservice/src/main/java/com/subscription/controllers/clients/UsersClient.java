package com.subscription.controllers.clients;


import com.subscription.domain.User;
import com.subscription.domain.Video;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client("${USERS_URL:`http://localhost:8080/users`}")
public interface UsersClient {

	@Get("/{id}")
	User getUser(long id);
	
	@Get("/{id}/watched")
	Iterable<Video> getUserWatchedVideos(long id);
	
}

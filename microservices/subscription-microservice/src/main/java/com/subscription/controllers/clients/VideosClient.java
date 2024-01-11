package com.subscription.controllers.clients;


import com.subscription.domain.User;
import com.subscription.domain.Video;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client("${VIDEOS_URL:`http://localhost:8080/videos`}")
public interface VideosClient {

	@Get("/{id}")
	public Video getVideo(long id);
	
	// Viewers

	@Get("/{id}/viewers")
	public Iterable<User> getViewers(long id);

}

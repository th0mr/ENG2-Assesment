package com.subscription.controllers.clients;

import com.subscription.domain.Hashtag;
import com.subscription.domain.Video;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client("${HASHTAGS_URL:`http://localhost:8080/hashtags`}")
public interface HashtagsClient {

	@Get("/{id}")
	Hashtag getHashtag(long id);
	
	@Get("/{id}/videos")
	Iterable<Video> getHashtagVideos(long id);
	
}


package com.video.clients;

import com.video.domain.Hashtag;
import com.video.domain.Video;
import com.video.dto.HashtagDTO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("${hashtag.url:`http://localhost:8080/hashtags`}")
public interface HashtagsClient {

	@Get("/")
	Iterable<Hashtag> list();

	// Not used
	@Post("/")
	HttpResponse<Void> add(@Body HashtagDTO hashtagDetails);

	// Not used
	@Get("/{id}")
	Hashtag getHashtag(long id);
	
	@Get("/{id}/videos")
	Iterable<Video> getHashtagVideos(long id);
	
	@Delete("/{id}")
	public HttpResponse<Void> deleteHashtag(long id);
	
}


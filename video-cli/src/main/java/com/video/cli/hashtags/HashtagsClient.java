package com.video.cli.hashtags;

import com.video.cli.domain.Hashtag;
import com.video.cli.domain.User;
import com.video.cli.domain.Video;
import com.video.cli.dto.HashtagDTO;
import com.video.cli.dto.UserDTO;
import io.micronaut.http.client.annotation.Client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;

@Client("${users.url:`http://localhost:8080/hashtags`}")
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
	
}


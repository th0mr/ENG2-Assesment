package com.thm.cli.trendingHashtags;

import com.thm.cli.domain.HashLikesPair;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("${users.url:`http://localhost:8081/trending-hashtags`}")
public interface TrendingHashtagsClient {

	@Get("/")
	Iterable<HashLikesPair> list();
	
}

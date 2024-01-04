package com.thm.clients;

import java.util.List;

import com.trendinghashtag.domain.HashLikesPair;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client("${users.url:`http://localhost:8081/trendingHashtags`}")
public interface TrendingHashtagsClient {

	@Get("/")
	List<HashLikesPair> list();
	
}

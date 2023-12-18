package com.video.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import jakarta.inject.Inject;

import com.video.domain.Hashtag;
import com.video.domain.User;
import com.video.domain.Video;
import com.video.dto.HashtagDTO;
import com.video.dto.VideoDTO;
import com.video.repositories.HashtagRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Controller("/hashtags")
public class HashtagsController {

    @Inject
    private HashtagRepository repo;

	@Get("/")
	public Iterable<Hashtag> list() {
		return repo.findAll();
	}
    
	public boolean exists(String name) {
		Optional<Hashtag> hashtag = repo.findByName(name);
		if (hashtag.isEmpty()) {
			return false;
		}
		return true;
	}
	
    @Post("/")
	public HttpResponse<Void> add(@Body HashtagDTO hashtagDetails) {
    	
    	// Check if hashtag already exists
    	if (exists(hashtagDetails.getName())) {
    		// exit as we dont need to create it again
    		return HttpResponse.ok();
    	}
    	
    	// The hashtag does not exist so we'll make it
		Hashtag hashtag = new Hashtag();
		hashtag.setName(hashtagDetails.getName());
		repo.save(hashtag);

		return HttpResponse.created(URI.create("/hashtags/" + hashtag.getId()));
	}

	@Get("/{id}")
	public Hashtag getHashtag(long id) {
		return repo.findOne(id).orElse(null);
	}
	
	@Transactional
	@Delete("/{id}")
	public HttpResponse<Void> deleteHashtag(long id) {
		Optional<Hashtag> hashtag = repo.findById(id);
		if (hashtag.isEmpty()) {
			return HttpResponse.notFound();
		}

		repo.delete(hashtag.get());
		return HttpResponse.ok();
	}	
	
	// Videos related code
	
	@Transactional
	@Get("/{id}/videos")
	public Iterable<Video> getVideos(long id) {
		Optional<Hashtag> hash = repo.findById(id);
		if (hash.isEmpty()) {
			return null;
		}
		return hash.get().getVideos();
	}
	
}
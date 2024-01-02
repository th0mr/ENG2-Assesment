package com.video.controllers;


import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;

import com.video.domain.User;
import com.video.domain.Video;
import com.video.dto.UserDTO;
import com.video.repositories.UsersRepository;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import jakarta.inject.Inject;

@Controller("/users")
public class UsersController {

	@Inject
	UsersRepository repo;

	@Get("/")
	public Iterable<User> list() {
		return repo.findAll();
	}

	@Post("/")
	public HttpResponse<Void> add(@Body UserDTO userDetails) {
		User user = new User();
		user.setUsername(userDetails.getUsername());
		repo.save(user);
		System.out.println("User: " + user.getUsername() + " created with ID=" + user.getId());
		return HttpResponse.created(URI.create("/users/" + user.getId()));
	}

	@Get("/{id}")
	public User getUser(long id) {
		return repo.findById(id).orElse(null);
	}

	@Transactional
	@Put("/{id}")
	public HttpResponse<Void> updateUser(long id, @Body UserDTO userDetails) {
		Optional<User> user = repo.findById(id);
		if (user.isEmpty()) {
			return HttpResponse.notFound();
		}

		User u = user.get();
		if (userDetails.getUsername() != null) {
			u.setUsername(userDetails.getUsername());
		}
		repo.save(u);
		return HttpResponse.ok();
	}

	@Transactional
	@Delete("/{id}")
	public HttpResponse<Void> deleteUser(long id) {
		Optional<User> user = repo.findById(id);
		if (user.isEmpty()) {
			return HttpResponse.notFound();
		}

		repo.delete(user.get());
		return HttpResponse.ok();
	}
	
	@Transactional
	@Get("/{id}/videos")
	public Iterable<Video> getVideos(long id) {
		Optional<User> user = repo.findById(id);
		if (user.isEmpty()) {
			return null;
		}
		return user.get().getVideos();
	}
	
	@Transactional
	@Get("/{id}/liked")
	public Iterable<Video> getLikedVideos(long id) {
		Optional<User> user = repo.findById(id);
		if (user.isEmpty()) {
			return null;
		}
		return user.get().getLikedVideos();
	}
	
	@Transactional
	@Get("/{id}/disliked")
	public Iterable<Video> getDislikedVideos(long id) {
		Optional<User> user = repo.findById(id);
		if (user.isEmpty()) {
			return null;
		}
		return user.get().getDislikedVideos();
	}
	
	@Transactional
	@Get("/{id}/watched")
	public Iterable<Video> getWatchedVideos(long id) {
		Optional<User> user = repo.findById(id);
		if (user.isEmpty()) {
			return null;
		}
		return user.get().getWatchedVideos();
	}

}

package com.video.controllers;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;

import com.video.domain.User;
import com.video.domain.Video;
import com.video.dto.VideoDTO;
import com.video.events.VideosProducer;
import com.video.repositories.UsersRepository;
import com.video.repositories.VideosRepository;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import jakarta.inject.Inject;

@Controller("/videos")
public class VideosController {

	@Inject
	VideosRepository repo;

	@Inject
	UsersRepository userRepo;
	
	@Inject
	VideosProducer videosProducer;

	@Get("/")
	public Iterable<Video> list() {
		return repo.findAll();
	}

	// Video Related Code
	
	@Post("/")
	public HttpResponse<Void> add(@Body VideoDTO videoDetails) {
		Video video = new Video();
		video.setTitle(videoDetails.getTitle());
		video.setCreatorId(videoDetails.getCreatorId());
		video.setHashtags(videoDetails.getHashtags());

		repo.save(video);

		return HttpResponse.created(URI.create("/videos/" + video.getId()));
	}

	@Get("/{id}")
	public VideoDTO getVideo(long id) {
		return repo.findOne(id).orElse(null);
	}

	@Transactional
	@Put("/{id}")
	public HttpResponse<Void> updateVideo(long id, @Body VideoDTO videoDetails) {
		Optional<Video> video = repo.findById(id);
		if (video.isEmpty()) {
			return HttpResponse.notFound();
		}

		Video v = video.get();
		if (videoDetails.getTitle() != null) {
			v.setTitle(videoDetails.getTitle());
		}
		if (videoDetails.getCreatorId() != null) {
			v.setCreatorId(videoDetails.getCreatorId());
		}
		if (videoDetails.getHashtags() != null) {
			v.setHashtags(videoDetails.getHashtags());
		}
		repo.update(v);
		return HttpResponse.ok();
	}

	@Transactional
	@Delete("/{id}")
	public HttpResponse<Void> deleteVideo(long id) {
		Optional<Video> video = repo.findById(id);
		if (video.isEmpty()) {
			return HttpResponse.notFound();
		}

		repo.delete(video.get());
		return HttpResponse.ok();
	}

	// Viewer Related Code
	
	@Get("/{id}/viewers")
	public Iterable<User> getViewers(long id) {
		Optional<Video> video = repo.findById(id);
		if (video.isEmpty()) {
			return null;
		}
		return video.get().getViewers();
	}

	@Transactional
	@Put("/{videoId}/viewers/{userId}")
	public HttpResponse<String> addViewer(long videoId, long userId) {
		Optional<Video> oVideo = repo.findById(videoId);
		if (oVideo.isEmpty()) {
			return HttpResponse.notFound(String.format("Video %d not found", videoId));
		}

		Optional<User> oUser = userRepo.findById(userId);
		if (oUser.isEmpty()) {
			return HttpResponse.notFound(String.format("User %d not found", userId));
		}

		Video video = oVideo.get();
		boolean success = video.getViewers().add(oUser.get());
		repo.update(video);

//		if (success) {
//			videosProducer.watchedVideo(userId, video);
//		}
		
		return HttpResponse.ok(String.format("User %d added as viewer of video %d", userId, videoId));
	}

	@Transactional
	@Delete("/{videoId}/viewers/{userId}")
	public HttpResponse<String> removeViewer(long videoId, long userId) {
		Optional<Video> oVideo = repo.findById(videoId);
		if (oVideo.isEmpty()) {
			return HttpResponse.notFound(String.format("Video %d not found", videoId));
		}

		Video video = oVideo.get();
		video.getViewers().removeIf(u -> userId == u.getId());
		repo.update(video);

		return HttpResponse.ok();
	}
	
	// Like / Dislike Related Code

	@Get("/{id}/likers")
	public Iterable<User> getLikers(long id) {
		Optional<Video> video = repo.findById(id);
		if (video.isEmpty()) {
			return null;
		}
		return video.get().getLikers();
	}
	
	@Get("/{id}/dislikers")
	public Iterable<User> getDislikers(long id) {
		Optional<Video> video = repo.findById(id);
		if (video.isEmpty()) {
			return null;
		}
		return video.get().getDislikers();
	}

	@Transactional
	@Put("/{videoId}/likers/{userId}")
	public HttpResponse<String> addLiker(long videoId, long userId) {
		Optional<Video> oVideo = repo.findById(videoId);
		if (oVideo.isEmpty()) {
			return HttpResponse.notFound(String.format("Video %d not found", videoId));
		}

		Optional<User> oUser = userRepo.findById(userId);
		if (oUser.isEmpty()) {
			return HttpResponse.notFound(String.format("User %d not found", userId));
		}

		Video video = oVideo.get();
		boolean success = video.getLikers().add(oUser.get());
		repo.update(video);

		if (success) {
			videosProducer.likedVideo(userId, video);
		}
		
		return HttpResponse.ok(String.format("User %d added as liker of video %d", userId, videoId));
	}
	
	@Transactional
	@Put("/{videoId}/dislikers/{userId}")
	public HttpResponse<String> addDisiker(long videoId, long userId) {
		Optional<Video> oVideo = repo.findById(videoId);
		if (oVideo.isEmpty()) {
			return HttpResponse.notFound(String.format("Video %d not found", videoId));
		}

		Optional<User> oUser = userRepo.findById(userId);
		if (oUser.isEmpty()) {
			return HttpResponse.notFound(String.format("User %d not found", userId));
		}

		Video video = oVideo.get();
		boolean success = video.getDislikers().add(oUser.get());
		repo.update(video);

		if (success) {
			videosProducer.dislikedVideo(userId, video);
		}
		
		return HttpResponse.ok(String.format("User %d added as disliker of video %d", userId, videoId));
	}

	@Transactional
	@Delete("/{videoId}/likers/{userId}")
	public HttpResponse<String> removeLiker(long videoId, long userId) {
		Optional<Video> oVideo = repo.findById(videoId);
		if (oVideo.isEmpty()) {
			return HttpResponse.notFound(String.format("Video %d not found", videoId));
		}

		Video video = oVideo.get();
		video.getLikers().removeIf(u -> userId == u.getId());
		repo.update(video);

		return HttpResponse.ok();
	}
	
	@Transactional
	@Delete("/{videoId}/dislikers/{userId}")
	public HttpResponse<String> removeDisliker(long videoId, long userId) {
		Optional<Video> oVideo = repo.findById(videoId);
		if (oVideo.isEmpty()) {
			return HttpResponse.notFound(String.format("Video %d not found", videoId));
		}

		Video video = oVideo.get();
		video.getDislikers().removeIf(u -> userId == u.getId());
		repo.update(video);

		return HttpResponse.ok();
	}
	
}


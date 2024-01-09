package com.video.controllers;

import java.net.URI;
import java.util.HashSet;
import java.util.Optional;

import javax.transaction.Transactional;

import com.video.domain.Hashtag;
import com.video.domain.User;
import com.video.domain.Video;
import com.video.dto.HashtagDTO;
import com.video.dto.VideoDTO;
import com.video.events.VideoProducer;
import com.video.repositories.HashtagRepository;
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
    HashtagRepository hashtagRepo;
	
	@Inject
	VideoProducer videosProducer;
	
	@Inject
	HashtagsController hashtagController;
	
	@Get("/")
	public Iterable<Video> list() {
		return repo.findAll();
	}

	// Video Related Code
	
	@Post("/")
	public HttpResponse<Void> add(@Body VideoDTO videoDetails) {
		Video video = new Video();
		
		// Set title
		String title = videoDetails.getTitle();
		video.setTitle(title);
		
		
		// Set User
		
		long creatorId = videoDetails.getCreatorId();
		User user = userRepo.findById(creatorId).orElse(null);
		if (user == null) {
			System.err.println("Video not created as the user with id=" + creatorId + "was not found!");
			return HttpResponse.notFound();
		}
		video.setCreator(user);

		URI uri = URI.create("/videos/" + video.getId());
        Video savedVideo = repo.save(video);
        
		String hashtagString = videoDetails.getHashtagString();
		// Set hashtags
		if (hashtagString != null) {
			// Split the string by commas
	        String[] hashtagsArray = hashtagString.split(",");

	        // Iterate through each hashtag and find the actual Hashtag entity (creating it if it does not exist)
	        for (String hashtagName : hashtagsArray) {
	        	HashtagDTO hashDTO = new HashtagDTO();
	        	hashDTO.setName(hashtagName);
	        	// At the controller level, logic is checking that this is not producing duplicate hashtags with the same name
	        	hashtagController.add(hashDTO);
	        	// Now obtain the hashtag we've created
	        	Hashtag hash = hashtagRepo.findByName(hashtagName).orElse(null);
	        	addHashtag(savedVideo.getId(), hash.getId());
	        }
		}
		
		// Now get the video we just updated from the repo, now with hashtags, and 
        if (savedVideo != null) {
        	savedVideo = repo.findById(savedVideo.getId()).get();
			videosProducer.postedVideo(user.getId(), savedVideo);
		}
        
		return HttpResponse.created(uri);
	}

	@Get("/{id}")
	public Video getVideo(long id) {
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
		
		// long defaults to 0, the first id assigned by mariadb is a 1, so this shouldnt be an issue
		// but yes, this is lazy, and its too late to go change long -> Long for null checking
		if (videoDetails.getCreatorId() != 0) {
			long creatorId = videoDetails.getCreatorId();
			User user = userRepo.findById(creatorId).orElse(null);
			if (user == null) {
				System.err.println("Video not updated as the user with id=" + creatorId + " was not found!");
				return HttpResponse.notFound();
			}
			v.setCreator(user);
		}
		
		
		if (videoDetails.getHashtagString() != null) {
			String hashtagString = videoDetails.getHashtagString();
			// Split the string by commas
	        String[] hashtagsArray = hashtagString.split(",");

	        // Wipe clean the hashtag set
	        v.setHashtags(new HashSet<>());
	        
	        // Iterate through each hashtag and find the actual Hashtag entity (creating it if it does not exist)
	        for (String hashtagName : hashtagsArray) {
	        	HashtagDTO hashDTO = new HashtagDTO();
	        	hashDTO.setName(hashtagName);
	        	// At the controller level, logic is checking that this is not producing duplicate hashtags with the same name
	        	hashtagController.add(hashDTO);
	        	// Now obtain the hashtag we've created
	        	Hashtag hash = hashtagRepo.findByName(hashtagName).orElse(null);
	        	addHashtag(v.getId(), hash.getId());
	        }
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
	
	// Hashtag Related Code
	
	@Transactional
	@Get("/{id}/hashtags")
	public Iterable<Hashtag> getHashtags(long id) {
		Optional<Video> video = repo.findById(id);
		if (video.isEmpty()) {
			return null;
		}
		return video.get().getHashtags();
	}
	
	@Transactional
	@Put("/{videoId}/hashtags/{hashtagId}")
	public HttpResponse<String> addHashtag(long videoId, long hashtagId) {
		Optional<Video> oVideo = repo.findById(videoId);
		if (oVideo.isEmpty()) {
			return HttpResponse.notFound(String.format("Video %d not found", videoId));
		}

		Optional<Hashtag> oHashtag = hashtagRepo.findById(hashtagId);
		if (oHashtag.isEmpty()) {
			return HttpResponse.notFound(String.format("Hashtag %d not found", hashtagId));
		}

		Video video = oVideo.get();
		Hashtag hashtag = oHashtag.get();
		video.getHashtags().add(hashtag);
		
		repo.update(video);
		
		return HttpResponse.ok(String.format("Hashtag %d added as a hashtag of video %d", hashtagId, videoId));
	}
	
	// Viewer Related Code
	
	@Transactional
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

		if (success) {
			videosProducer.watchedVideo(userId, video);
		}
		
		return HttpResponse.ok(String.format("User %d added as viewer of video %d", userId, videoId));
	}

	@Transactional
	@Delete("/{videoId}/viewers/{userId}")
	public HttpResponse<String> removeViewer(long videoId, long userId) {
		Optional<Video> oVideo = repo.findById(videoId);
		if (oVideo.isEmpty()) {
			return HttpResponse.notFound(String.format("Video %d not found", videoId));
		}
		
		Optional<User> oUser = userRepo.findById(userId);
		if (oUser.isEmpty()) {
			return HttpResponse.notFound(String.format("User %d not found", userId));
		}

		Video video = oVideo.get();
		video.getViewers().removeIf(u -> userId == u.getId());
		repo.update(video);

		return HttpResponse.ok();
	}
	
	// Like / Dislike Related Code

	@Transactional
	@Get("/{id}/likers")
	public Iterable<User> getLikers(long id) {
		Optional<Video> video = repo.findById(id);
		if (video.isEmpty()) {
			return null;
		}
		return video.get().getLikers();
	}
	
	@Transactional
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
	public HttpResponse<String> addDisliker(long videoId, long userId) {
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

		Optional<User> oUser = userRepo.findById(userId);
		if (oUser.isEmpty()) {
			return HttpResponse.notFound(String.format("User %d not found", userId));
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
		
		Optional<User> oUser = userRepo.findById(userId);
		if (oUser.isEmpty()) {
			return HttpResponse.notFound(String.format("User %d not found", userId));
		}

		Video video = oVideo.get();
		video.getDislikers().removeIf(u -> userId == u.getId());
		repo.update(video);

		return HttpResponse.ok();
	}
	
}


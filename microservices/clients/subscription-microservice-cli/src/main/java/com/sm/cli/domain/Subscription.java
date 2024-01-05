package com.sm.cli.domain;

import java.util.HashSet;
import java.util.Set;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Subscription {

	// This is the id of the subscription
    private Long id;
    
    // The user that is subscribing
    private Long userId;
    
	// The hashtag they have subscribed to
    private Long hashtagId;
    
    // These are the videos that we build the top ten from
    private Set<Video> videosNotSeen = new HashSet<Video>();

	// The videos that have been watched since that user subscribed
    private Set<Video> videosSeenSinceSub = new HashSet<Video>();
    
    // The videos that have been posted since the user has subscribed to that tag
    private Set<Video> videosPostedSinceSub = new HashSet<Video>();
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getHashtagId() {
		return hashtagId;
	}

	public void setHashtagId(Long hashtagId) {
		this.hashtagId = hashtagId;
	}

    public Set<Video> getVideosNotSeen() {
		return videosNotSeen;
	}

	public void setVideosNotSeen(Set<Video> videosNotSeen) {
		this.videosNotSeen = videosNotSeen;
	}

	public Set<Video> getVideosSeenSinceSub() {
		return videosSeenSinceSub;
	}

	public void setVideosSeenSinceSub(Set<Video> videosSeenSinceSub) {
		this.videosSeenSinceSub = videosSeenSinceSub;
	}

	public Set<Video> getVideosPostedSinceSub() {
		return videosPostedSinceSub;
	}

	public void setVideosPostedSinceSub(Set<Video> videosPostedSinceSub) {
		this.videosPostedSinceSub = videosPostedSinceSub;
	}
	
	@Override
	public String toString() {
		return "Subscription [id=" + id + ", userId=" + userId + ", hashtagId=" + hashtagId + "]";
	}
}

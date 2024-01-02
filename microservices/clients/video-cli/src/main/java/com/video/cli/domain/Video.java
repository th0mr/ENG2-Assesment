package com.video.cli.domain;

import java.util.HashSet;
import java.util.Set;

import com.video.cli.domain.Hashtag;
import com.video.cli.domain.User;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Video {

	private Long id;
	
	private String title;
	
	private User creator;
	
	private Set<Hashtag> hashtags;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Set<Hashtag> getHashtags() {
		return hashtags;
	}

	public void setHashtags(Set<Hashtag> hashtags) {
		this.hashtags = hashtags;
	}

	@Override
	public String toString() {
		return "Video [ID=" + id + ", title=" + title + ", creatorId=" + creator.toString() + ", hashtags=" + hashtags.toString() + "]";
	}
	
}

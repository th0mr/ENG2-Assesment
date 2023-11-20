package com.video.cli.domain;

import java.util.Set;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Video {

	private Long id;
	
	private String title;
	
	private Long creatorId;
	
	private String[] hashtags;
	
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
	
	public Long getCreatorId() {
		return creatorId;
	}

	// Using ID to set creator
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	
	public String[] getHashtags(){
		return hashtags;
	}
	
	public void setHashtags(String[] hashtags) {
		this.hashtags = hashtags;
	}

	@Override
	public String toString() {
		return "Video [ID=" + id + ", title=" + title + ", creatorId=" + creatorId + ", hashtags=" + hashtags.toString() + "]";
	}
	
}

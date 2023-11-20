package com.video.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class VideoDTO {

	private String title;
	private Long creatorId;
	private String[] hashtags;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getCreatorId() {
		return creatorId;
	}

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
		return "VideoDTO [title=" + title + ", creatorId=" + creatorId + ", hashtags=" + hashtags.toString() + "]";
	}
}

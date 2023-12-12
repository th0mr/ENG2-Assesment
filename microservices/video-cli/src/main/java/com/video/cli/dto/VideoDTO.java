package com.video.cli.dto;

import java.util.HashSet;
import java.util.Set;

import com.video.cli.domain.Hashtag;
import com.video.cli.domain.User;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class VideoDTO {

	private String title;
	private long creatorId;
	private String hashtagString;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "VideoDTO [title=" + title + "]";
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public String getHashtagString() {
		return hashtagString;
	}

	public void setHashtagString(String hashtagString) {
		this.hashtagString = hashtagString;
	}
}


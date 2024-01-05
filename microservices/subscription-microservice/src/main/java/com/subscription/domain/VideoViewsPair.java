package com.subscription.domain;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class VideoViewsPair {
	
	private Video video;
	private int views;
	
	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public int getViews() {
		return views;
	}
	
	public void setViews(int views) {
		this.views = views;
	}
	
	@Override
	public String toString() {
		return "VideoViewsPair [video=" + video + ", views=" + views + "]";
	}		
	
}

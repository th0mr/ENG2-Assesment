package com.video.cli.domain;

import java.util.HashSet;
import java.util.Set;

import com.video.cli.domain.Video;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Hashtag {

    private Long id;

    private String name;
    
    private Set<Video> videos;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Set<Video> getVideos() {
		return videos;
	}

	public void setVideos(Set<Video> videos) {
		this.videos = videos;
	}
	
	@Override
	public String toString() {
		return "Hashtag [id=" + id + ", name=" + name + "]";
	}
    
}
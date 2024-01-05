package com.sm.cli.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class User {

	private Long id;

	private String username;
	
	@JsonIgnore
    private Set<Video> videos = new HashSet<>();

	@JsonIgnore
	private Set<Video> watchedVideos = new HashSet<>();
	
	@JsonIgnore
	private Set<Video> likedVideos = new HashSet<>();
	
	@JsonIgnore
	private Set<Video> dislikedVideos = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
    public Set<Video> getVideos() {
        return videos;
    }

    public void setVideos(Set<Video> videos) {
        this.videos = videos;
    }

	public Set<Video> getWatchedVideos() {
		return watchedVideos;
	}

	public void setWatchedVideos(Set<Video> watchedVideos) {
		this.watchedVideos = watchedVideos;
	}

	public Set<Video> getLikedVideos() {
		return likedVideos;
	}

	public void setLikedVideos(Set<Video> likedVideos) {
		this.likedVideos = likedVideos;
	}

	public Set<Video> getDislikedVideos() {
		return dislikedVideos;
	}

	public void setDislikedVideos(Set<Video> dislikedVideos) {
		this.dislikedVideos = dislikedVideos;
	}

	@Override
	public String toString() {
		return "User[ID= " + id + ", username=" + username + "]";
	}
}

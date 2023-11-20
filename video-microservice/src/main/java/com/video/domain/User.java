package com.video.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.micronaut.serde.annotation.Serdeable;

@Entity
@Serdeable
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String username;

	@JsonIgnore
	@ManyToMany(mappedBy = "viewers")
	private Set<Video> watchedVideos;
	
//	@JsonIgnore
//	@ManyToMany(mappedBy = "likers")
//	private Set<Video> likedVideos;
//	
//	@JsonIgnore
//	@ManyToMany(mappedBy = "dislikers")
//	private Set<Video> dislikedVideos;

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

	public Set<Video> getWatchedVideos() {
		return watchedVideos;
	}

	public void setWatchedVideos(Set<Video> watchedVideos) {
		this.watchedVideos = watchedVideos;
	}

//	public Set<Video> getLikedVideos() {
//		return likedVideos;
//	}
//
//	public void setLikedVideos(Set<Video> likedVideos) {
//		this.likedVideos = likedVideos;
//	}
//
//	public Set<Video> getDislikedVideos() {
//		return dislikedVideos;
//	}
//
//	public void setDislikedVideos(Set<Video> dislikedVideos) {
//		this.dislikedVideos = dislikedVideos;
//	}

	@Override
	public String toString() {
		return "User[ID= " + id + ", username=" + username + "]";
	}
}

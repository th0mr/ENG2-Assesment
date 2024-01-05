package com.sm.cli.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.micronaut.serde.annotation.Serdeable;


@Serdeable
public class Video {

	private Long id;
	
	private String title;
	
	private User creator;
	
    private Set<Hashtag> hashtags = new HashSet<>();

	@JsonIgnore
	private Set<User> viewers = new HashSet<>();
	
	@JsonIgnore
	private Set<User> likers = new HashSet<>();
	
	@JsonIgnore

	private Set<User> dislikers = new HashSet<>();
	
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
	
	public Set<Hashtag> getHashtags(){
		return hashtags;
	}
	
	public void setHashtags(Set<Hashtag> hashtags) {
		this.hashtags = hashtags;
	}
	
	public Set<User> getViewers() {
		return viewers;
	}

	public void setViewers(Set<User> viewers) {
		this.viewers = viewers;
	}
	
	public Set<User> getLikers() {
		return likers;
	}

	public void setLikers(Set<User> likers) {
		this.likers = likers;
	}
	
	public Set<User> getDislikers() {
		return dislikers;
	}

	public void setDislikers(Set<User> dislikers) {
		this.dislikers = dislikers;
	}

	@Override
	public String toString() {
		return "Video [ID=" + id + ", title=" + title + ", creator=" + creator.toString() + "]";
	}
	
}

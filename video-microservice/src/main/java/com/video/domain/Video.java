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
public class Video {

	@Id
	@GeneratedValue 
	private Long id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private Long creatorId;
	
	// Could this be nullable true?
	@Column(nullable = false)
	private String[] hashtags;

	@JsonIgnore
	@ManyToMany
	private Set<User> viewers;
	
	@JsonIgnore
	@ManyToMany
	private Set<User> likers;
	
	@JsonIgnore
	@ManyToMany
	private Set<User> dislikers;
	
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
		return "Video [ID=" + id + ", title=" + title + ", creatorId=" + creatorId + ", hashtags=" + hashtags.toString() + "]";
	}
	
}

package com.thm.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@Entity
public class HashtagLikedDislikedEvent {

    @Id
    @GeneratedValue()
    private Long id;

    private String hashtagName;
	private int value; // +1 for like, -1 for dislike
    private LocalDateTime timestamp;
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHashtagName() {
		return hashtagName;
	}
	public void setHashtagName(String hashtagName) {
		this.hashtagName = hashtagName;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return "HashtagLikedDislikedEvent [id=" + id + ", hashtagName=" + hashtagName + ", value=" + value
				+ ", timestamp=" + timestamp + "]";
	}
	
}
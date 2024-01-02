package com.subscription.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.micronaut.serde.annotation.Serdeable;

@Entity
@Serdeable
public class Subscription {

	// This is the id of the subscription
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    // The user that is subscribing
    private Long userId;
    
	// The hashtag they have subscribed to
    private Long hashtagId;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getHashtagId() {
		return hashtagId;
	}

	public void setHashtagId(Long hashtagId) {
		this.hashtagId = hashtagId;
	}

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", userId=" + userId + ", hashtagId=" + hashtagId + "]";
	}
}
